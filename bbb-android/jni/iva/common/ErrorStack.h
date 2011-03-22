#ifndef _ERROR_QUEUE_H_
#define _ERROR_QUEUE_H_

#include <stack>
#include "ErrorData.h"
#include <Mutex.h>

using namespace std;

/**
 * \brief Abstração da pilha com os erros.
 */
class ErrorStack :
 stack<ErrorData>
{

 public:

	 /**
	  * Construtor padrão.
	  */
	 ErrorStack();

	 /**
	  * Destrutor padrão.
	  */
	 ~ErrorStack();

	 /**
	  * \brief Insere um erro no topo da pilha.
	  *
	  * \param data Erro a ser inserido.
	  */
	 void push(ErrorData & data);

	 /**
	  * \brief Retira o elemento do topo da pilha.
	  *
	  * \return Elemento do topo da pilha.
	  */
	 ErrorData & pop();

	 /**
	  * \brief Verifica se a pilha está vazia.
	  *
	  * \return Pilha está vazia?
	  * \retval true Pilha está vazia.
	  * \retval false Pilha tem elementos.
	  */
	 bool empty();

	 /**
	  * \brief Limpa a fila, garantindo exclusão mútua.
	  */
	 void clean();

	 /**
	  * \brief Saída via stream.
	  *
	  * Utilizado para passar o ErrorStack inteiro para uma stream.
	  *
	  * \warning Limpa toda a pilha que está sendo escrita.
	  *
	  * \param out Stream para a qual a pilha está sendo escrita.
	  * \param print Pilha que deve ser passada para o stream.
	  * \return Stream em que está sendo escrito.
	  */
	 friend ostream & operator<<(ostream & out, const ErrorStack & print);

 private:

	 /**
	  * \brief Limpa a fila, sem garantir exclusão mútua.
	  */
	 void clear();

	 Mutex mutex_; ///< \brief Mutex utilizado para o acesso sincronizado a pilha.

	 ErrorData toPop; ///< \brief Elemento sendo extraído da pilha.

};

#endif

