#ifdef _DEBUG
#define _CRTDBG_MAP_ALLOC
#include <stdlib.h>
#include <crtdbg.h>
#endif

#include <fstream>
#include <sstream>
#include <vector>
using namespace std;
#include <common.h>
#include "EncodeVideoH264Presets.h"

EncodeVideoH264Presets::EncodeVideoH264Presets()
{
}

int EncodeVideoH264Presets::parse(string filename, AVCodecContext * context)
{
    ifstream myfile;

    if (!context) {
        NEW_ERROR(E_COMMON_NULL_PARAMETER, "context");
        return E_COMMON_NULL_PARAMETER;
    }
    myfile.open(filename.c_str());
    if (myfile.fail()) {
        ErrorData err(E_ENCODE_PRESET_NOT_FOUND, Location(AT));
        err << "Erro ao abrir o arquivo de presets " << filename.c_str();
        err.pushError();
        return E_ENCODE_PRESET_NOT_FOUND;
    }

    // faz o parse
    return _Parse(myfile, context);
}

int EncodeVideoH264Presets::_Parse(istream & myfile, AVCodecContext * context)
{
    string str;
    int err = E_OK;

    while (myfile >> str) {
        err = _ParseLine(str, context);
        if (err != E_OK) break;
    }

    return err;
}

int EncodeVideoH264Presets::_ParseLine(string line, AVCodecContext * context)
{
    int err;
    IvaString param;
    IvaString values;

    // encontra o sinal = que divide o parâmetro do seu valor
    size_t found = line.find('=');
    if (found == string::npos) {
        ErrorData err(E_ERROR, Location(AT));
        err << "Erro na leitura do preset, linha " << line;
        err.pushError();
        return E_ERROR;
    }

    // pega parâmetro e valor
    param = line.substr(0, found);
    values = line.substr(found + 1);
    //printf("\t param = %s, ", param.c_str());
    //printf(" values = %s \n", values.c_str());
    if (param.empty() || values.empty()) {
        ErrorData err(E_ERROR, Location(AT));
        err << "Erro no preset: linha malformada " << line;
        err.pushError();
        return E_ERROR;
    }

    param.trim();
    values.trim();

    // busca a opção correspondente ao parâmetro
    AVOption * option = _FindOption(param);
    if (!option) return E_ERROR;

    int flags = 0;
    void * dst = ((uint8_t*)context) + option->offset;
    err = E_OK;

    // trata o valor conforme o tipo do parâmetro
    switch (option->type) {
        case FF_OPT_TYPE_FLAGS:
            flags = (int)option->default_val;
            err = _GetFlags(values, &flags);
            *(int *)dst = flags;
            break;
        case FF_OPT_TYPE_INT:
            err = _SetValue<int>(values, dst);
            break;
        case FF_OPT_TYPE_INT64:
            err = _SetValue<int64_t>(values, dst);
            break;
        case FF_OPT_TYPE_DOUBLE:
            err = _SetValue<double>(values, dst);
            break;
        case FF_OPT_TYPE_FLOAT:
            err = _SetValue<float>(values, dst);
            break;

        // tipos abaixo não são suportados pois não são necessários atualmente
        // poucos parâmetros são deste tipo e eles não são importantes para o h264
        // se encontrar algum deles não assume como erro, só mostra msg e segue com o parse
        case FF_OPT_TYPE_STRING:
            NEW_WARNING(E_ERROR, "Tipo do parâmetro não é suportado pelo parser \"FF_OPT_TYPE_STRING\"");
            break;
        case FF_OPT_TYPE_RATIONAL:
            NEW_WARNING(E_ERROR, "Tipo do parâmetro não é suportado pelo parser \"FF_OPT_TYPE_RATIONAL\"");
            break;
        case FF_OPT_TYPE_BINARY:
            NEW_WARNING(E_ERROR, "Tipo do parâmetro não é suportado pelo parser \"FF_OPT_TYPE_BINARY\"");
            break;
        case FF_OPT_TYPE_CONST:
            NEW_WARNING(E_ERROR, "Tipo do parâmetro não é suportado pelo parser \"FF_OPT_TYPE_CONST\"");
            break;
    }
    if (err != E_OK) {
        ErrorData err(E_ERROR, Location(AT));
        err << "Erro no preset: valores da linha " << line;
        err.pushError();
    }
    return err;
}

AVOption * EncodeVideoH264Presets::_FindOption(string name)
{
    int i = 1;
    AVOption * o = (AVOption *)&(EncodeVideoH264Opt::options[0]);
    while (o && o->name) {
        if (!strcmp(o->name, name.c_str())) {
            return o;
        }
        o = (AVOption *)&(EncodeVideoH264Opt::options[i++]);
    }
    return NULL;
}

template <typename T>
int EncodeVideoH264Presets::_CheckFlagOrValueAndSet(string value, void * pointer)
{
    int err = E_OK;

    string str = value;

    // busca o sinal: sinal indica que é uma flag
    bool negative = false;
    bool flag = true;
    if (str.at(0) == '-') {
        str = str.substr(1); // corta o primeiro caractere
        negative = true;     // indica que deve remover a flag
    } else if (str.at(0) == '+') {
        str = str.substr(1); // corta o primeiro caractere
        negative = false;    // indica que deve adicionar a flag
    } else {
        flag = false;        // se não tem sinal é um valor absoluto, não flag. deve setar ele
    }

    // cast para o tipo especifico dos dados
    T * data = (T *)pointer;

    // busca a option
    AVOption * o;
    o = _FindOption(str);
    if (o) { // achou
        if (flag) { // se é flag, liga ou desliga
            if (negative) {
                *(int *)data &= ~(int)o->default_val;
            } else {
                *(int *)data |= (int)o->default_val;
            }
        } else { // não é flag, seta o valor absoluto
            *data = (T)o->default_val;
        }
    } else {
        err = E_ERROR;
    }

    return err;
}

template <typename T>
int EncodeVideoH264Presets::_SetValue(string value, void * pointer)
{
    if (!pointer) return E_ERROR;

    int err = E_OK;

    T * data = (T *)pointer;

    istringstream myStream(value);
    if (!(myStream >> *data)) { // se falhou ao pegar com tipo natural, tenta pegar como option
        _CheckFlagOrValueAndSet<T>(value, pointer);
    }
    return err;
}

int EncodeVideoH264Presets::_BreakFlags(string value, vector<string> & vec)
{
    if (value.empty()) return E_ERROR;

    string strActual = "";
    size_t signActual = 0;
    size_t signLast = 0;

    // procura por sinais + e - na string de entrada
    // usa os sinais como separadores e coloca as strings geradas no vector de saída
    signActual = value.find_first_of("+-");
    while (signActual != string::npos) {
        strActual = value.substr(signLast, signActual - signLast);
        if (!strActual.empty()) {
            vec.push_back(strActual);
        }
        signLast = signActual;
        signActual = value.find_first_of("+-", signLast + 1);
    }
    // último valor: do último sinal até o fim
    // ou do início até o fim se não achou nenhum sinal
    strActual = value.substr(signLast);
    vec.push_back(strActual);

    return E_OK;
}

int EncodeVideoH264Presets::_GetFlags(string value, int * retValue)
{
    if (!retValue) return E_ERROR;
    if (value.empty()) return E_ERROR;

    // busca o vetor com as flags já separadas
    vector<string> vec;
    int err = _BreakFlags(value, vec);
    if (err != E_OK) {
        return E_ERROR;
    }

    // percorre a lista de flags e vai setando uma por uma
    vector<string>::iterator it;
    for (it = vec.begin(); it != vec.end(); ++it) {
        string str = (*it);
        _CheckFlagOrValueAndSet<int>(str, (void *)retValue);
    }

    return E_OK;
}

