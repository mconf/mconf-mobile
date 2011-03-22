#ifndef _ERROR_CONTROLLER_H_
#define _ERROR_CONTROLLER_H_

#include "ErrorStack.h"
#include "ErrorVector.h"

/**
 * Classe que controla o acesso a pilha de erros, centralizando
 * os dados sobre erros do sistema.
 */
class ErrorController : 
  public ErrorStack
{

 public:

     static const int ERROR_SUPRESS_SECS = 2;

	  /**
	   * \brief Construtor padrão.
	   */
	  ErrorController();

	  /**
	   * \brief Destrutor padrão.
	   */
	  ~ErrorController();

	  /**
	   * \brief Insere o erro \a newErr na pilha de erros como uma falha do sistema.
	   *
	   * \param newErr Dados do erro a ser inserido na pilha.
	   */
	  void newError(ErrorData & newErr);

	  /**
	   * \brief Insere o erro \a newWarn na pilha de erros como um indicador de
	   * atenção a possível falha.
	   *
	   * \param newWarn Dados do erro a ser inserido na pilha.
	   */
	  void newWarning(ErrorData & newWarn);

	  /**
	   * \brief Retorna o último erro inserido.
	   *
	   * \return Dados sobre o último erro inserido na pilha.
	   */
	  ErrorData & getLast();

	  /**
	   * \brief Procura a mensagem padrão para o erro \a error.
	   *
	   * \param error Os dados do erro.
	   * \return A mensagem padrão para o referido \a error.
	   */
	  IvaString & getDefaultMsg(ErrorData & error);

 private:
	  ErrorVector messagesVector; /**< Vetor com mensagens para os erros. */

};

extern ErrorController errorContext;

#endif
