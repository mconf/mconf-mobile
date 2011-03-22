
#ifndef _ERROR_DATA_H_
#define _ERROR_DATA_H_

#include <sstream>
#include <iostream>

using namespace std;

#include "IvaString.h"
#include "IvaTime.h"
#include "Location.h"
#include <Interval.h>

#define ERROR_TYPE_WARNING 2
#define ERROR_TYPE_ERROR   1
#define ERROR_TYPE_NONE    0

#define NEW_ERROR(code,message) (ErrorData(code,Location(AT),message).pushError())
#define NEW_WARNING(code,message) (ErrorData(code,Location(AT),message).pushWarning())

/**
 * Abstração para dados sobre erros.
 */
class ErrorData 
: public basic_stringstream<char, char_traits<char> >
{

	/**
	 * Buffer para armazenamento do stream.
	 */
	basic_stringbuf<char, char_traits<char> > _streambuffer;

 public:

	/**
	 * \brief Construtor de cópia.
	 *
	 * Copia os dados de \a operand para o novo objeto.
	 *
	 * \param operand Dados de erro a serem copiados.
	 */
	ErrorData(const ErrorData & operand);
  
	/**
	 * \brief Construtor.
	 *
	 * Cria o objeto com base nos dados passados e aguarda o stream com
	 * a mensagem de erro.
	 *
	 * \param code Código do erro.
	 * \param ocurrence Local em que ocorreu o erro.
	 */
	ErrorData(int code, Location ocurrence);

	/**
     * \brief Construtor.
	 *
	 * Cria o objeto com base nos dados passados
	 * (entre eles a mensagem de erro).
	 *
	 * \param code Código do erro.
	 * \param ocurrence Local em que ocorreu o erro.
	 * \param msg Mensagem de erro personalizada para o erro.
	 */
	ErrorData(int code, Location ocurrence, const IvaString& msg);
   
	/**
	 * \brief Construtor padrão.
	 */
	ErrorData();

	/**
	 * Destrutor padrão.
	 */
	~ErrorData();

	/**
	 * \brief Modifica o código de erro.
	 *
	 * \param code_in Novo código de erro.
	 */
	void setCode(int code_in);

	/**
	 * \brief Modifica o local do erro.
	 *
	 * \param newLocation Novo local do erro.
	 */
	void setLocation(Location newLocation);
  
	/**
	 * \brief Retorna o código de erro armazenado.
	 *
	 * \return Código de erro.
	 */
	int getCode();

	/**
	 * \brief Retorna o tipo do erro.
	 *
	 * \return O tipo do erro.
	 * \retval ERROR_TYPE_WARNING Exige atenção mas não é uma falha do sistema.
	 * \retval ERROR_TYPE_ERROR Falha no sistema.
	 */
	int getType();

	/**
	 * \brief Retorna a mensagem de erro customizada.
	 *
	 * \attention Não retorna a parte da mensagem gerada automaticamente.
	 *
	 * \return A mensagem de erro customizada.
	 */
	IvaString getMsg();

	/**
	 * \brief Atribuição.
	 *
	 * Atribui os dados em \a operand para o objeto corrente.
	 *
	 * \return O objeto corrente.
	 */
	ErrorData & operator= (ErrorData &operand);
  
	/**
	 * \brief Retorna a localização do erro.
	 *
	 * \return Localização do erro.
	 */
	Location & getLocation();
  
	/**
	 * \brief Coloca o erro na pilha de erros, setando ele como \a warning.
	 */
	void pushWarning();

	/**
	 * Coloca o erro na pilha de erros, setando ele como \a erro.
	 */
	void pushError();
  
	/**
	 * \brief Seta o ErrorData atual com os dados do topo da pilha de erros,
	 *
	 * \attention Elimina o elemento do topo da pilha.
	 */
	void getLast();

	/**
	 * \brief Retorna o momento em que o erro ocorreu.
	 *
	 * \return O momento em que o erro ocorreu.
	 */
	IvaTime & getMoment();

    /** \brief Retorna o timestamp em que o erro ocorreu.
     */
    Interval & getTimestamp();

	/**
	 * \brief Saída via stream.
	 *
	 * Utilizado para passar o IvaTime para uma stream.
	 *
	 * \param print Dados de erro a serem impressos.
	 * \param out Stream para a qual o elemento está sendo escrito.
	 * \return Stream em que está sendo escrito.
	 */
	friend ostream & operator<< (ostream & out, const ErrorData & print);

	/**
	 * \brief Retorna a mensagem de erro completa.
	 *
	 * \attention Retorna a parte da mensagem gerada automaticamente
	 * com base no código do erro.
	 *
	 * \return A mensagem de erro completa.
	 */
	IvaString getCompleteMsg();

 private:

	/**
	 * \brief Elimina os dados relativos ao tempo de criação do erro.
	 */
	void deleteTime();

	/**
	 * \brief Modifica o tempo do erro para o tempo atual.
	 */
	void setTime();

	/**
	 * \brief Tipo do erro.
	 *
	 * \retval ERROR_TYPE_WARNING Exige atenção mas não é uma falha do sistema.
	 * \retval ERROR_TYPE_ERROR Falha no sistema.
	 */
	int type_;

	int code_;              ///< Código do erro.
	IvaTime * time_;        ///< Momento em que ocorreu o erro.
	Location ocurrence_;    ///< Local em que o erro aconteceu.
    Interval timestamp_;    ///< Timestamp em que o erro aconteceu

};

#endif
