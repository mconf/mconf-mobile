#ifndef _ERROR_VECTOR_H_
#define _ERROR_VECTOR_H_

#include "IvaString.h"

/**
 * \brief Vetor que associa os erros às mensagens correspondentes.
 */
class ErrorVector :
public vector < vector < IvaString > > 
{

 public:
	/**
	 * \brief Construtor padrão.
	 *
	 * Gera os dados para o vetor.
	 *
	 */
	ErrorVector();

	/**
	 * \brief Destrutor padrão.
	 */
	~ErrorVector();

	/**
	 * \brief Dado um código de erro, retorna a mensagem correspondente.
	 *
	 * \param code O código de erro.
	 * \return String correspondente ao erro.
	 */
	IvaString & codeToMessage(int code);

	/**
	 * \brief Sobre-escrita de operador no estilo array.
	 *
	 * Dado um código de erro, retorna a mensagem correspondente.
	 *
	 * \param n O código de erro.
	 * \return String correspondente ao erro.
	 */
	IvaString & operator[] ( size_type n );

 private:

	IvaString nonError; ///< \brief String a ser retornada.

};

#endif
