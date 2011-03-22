#ifndef _LIST_INT_H_
#define _LIST_INT_H_

/********** Funções ************/

/**
 * \brief Cria novo elemento da lista com os dados passados.
 * \internal
 * \param data O dado a ser acoplado no elemento.
 * \param previous O elemento anterior da lista.
 * \param next O próximo elemento da lista.
 * \param error_code Código retornado em caso de erro.
 * \return Elemento criado.
 * \retval NULL Erro, ver código em error_code.
 *
 * Cria novo elemento, tendo data como o dado principal, previous como elemento anterior e
 * next como elemento posterior. Retorna este novo elemento.
 */
struct list2_element * createElement(void * data, struct list2_element * previous, 
				    struct list2_element * next, int * error_code);

/**
 * \brief Limpa um elemento da lista.
 * \param element Elemento a ser limpo.
 * \return Valor de sucesso ou erro (Por enquanto sempre sucesso).
 * \retval E_OK.
 * 
 * Limpa o elemento da lista. Por enquanto é só um free no elemento.
 */
int cleanElement(struct list2_element * element);

#endif
