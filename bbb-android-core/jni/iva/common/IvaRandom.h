#ifndef _IVA_RANDOM_H
#define _IVA_RANDOM_H

#include "Milliseconds.h"
#include <cstdlib>
using namespace std;

/** \brief Classe para geração de números pseudo-aleatórios
 *
 *  \todo Apesar de a classe ser construída com templates para suportar diversos
 *        tipos de números (16 bits, 32 bits, etc.), a geração é dependente do
 *        valor em RAND_MAX. Ou seja, mesmo que se solicite um número de 32 bits,
 *        o valor máximo gerado é RAND_MAX, que normalmente vale 32767.
 *
 *  \todo Função para gerar um número aleatório passando limite inferior e superior.
 */
template <class Type>
class IvaRandom
{
public:
    IvaRandom()
    {
        srand(Milliseconds().getTime());
        _Generate();
    };

    Type get()
    {
        return _value;
    };

private:
    Type _value;

    void _Generate()
    {
        _value = rand();
    };

};

#endif
