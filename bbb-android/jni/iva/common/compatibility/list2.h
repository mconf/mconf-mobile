#ifndef _LIST_H_
#define _LIST_H_


/********* Estruturas **********/

/** 
 * \brief Estrutura para um elemento da lista encadeada.
 */
struct list2_element {
  
  void                *elementData;   /**< O dado propriamente                */
  struct list2_element *next;	      /**< O próximo elemento da lista        */
  struct list2_element *previous;      /**< O elemento anterior da lista       */
  
};

/** 
 * \brief Estrutura para definição de uma lista encadeada. 
 */
struct list2 {
    
    struct list2_element *first;		    /**< Primeiro elemento da lista   */
    struct list2_element *last;          /**< Último elemento da lista     */
    struct list2_element *current;       /**< Elemento atual da lista      */
    int                 numElements;	/**< Número de elementos na lista */
    
};

/********** Funções ************/

/**
 * \brief Inicializa lista.
 * \param list Lista a ser inicializada.
 * \return Retorna um valor de sucesso ou erro.
 * \retval E_OK Retorno em caso de sucesso.
 * 
 * Inicializa a estrutura de dados referente a uma lista.
 */
int list2_init(struct list2 * listContext);

/**
 * \brief Finaliza a lista.
 * \param list Lista a ser finalizada.
 * \return Retorna um valor de sucesso ou erro.
 * \retval E_OK Retorno em caso de sucesso.
 * 
 * Remove todos os itens e finaliza a estrutura de dados referente a uma lista.
 */
int list2_end(struct list2 * listContext);

/**
 * \brief Adiciona elemento à lista.
 * \param list Lista na qual o elemento será adicionado.
 * \param data Dados a serem inseridos.
 * \return Retorna um valor de sucesso ou erro.
 * \retval E_OK Em caso de sucesso
 * \retval E_NULL_PARAMETER Algum dos parâmetros passados está com valor nulo.
 *
 * Adiciona elemento à lista e atualiza estrutura de dados da mesma.
 */
int list2_addElement(struct list2 * listContext, void * data);

/**
 * \brief Remove elemento da lista.
 * \param list Lista da qual o elemento será removido.
 * \return Retorna um valor de sucesso ou erro.
 * \retval E_OK Em caso de sucesso
 * \retval OUTRO Erro
 *
 * Remove elemento à lista e atualiza estrutura de dados da mesma.
 */
int list2_removeCurrentElement(struct list2 * listContext);

/**
 * \brief Procura elemento na lista e o torna elemento corrente.
 * \param list Lista na qual o elemento será procurado.
 * \return Retorna valor de erro ou sucesso.
 * \retval E_OK Em caso de sucesso
 * \retval OUTRO Erro
 *
 * Procura o elemento data na lista sendo que esse passará a ser o elemento corrente da lista.
 */
int list2_findElement(struct list2 * listContext, void * query_data);

/**
 * \brief Salva o primeiro elemento da lista como corrente.
 * \param list Lista na qual o elemento será procurado.
 * \return Retorna o elemento encontrado.
 * \retval E_OK Em caso de sucesso
 * \retval E_NO_ELEMENTS Não há elementos na lista.
 * \retval OUTRO Em caso de erro.
 */
int list2_firstElement(struct list2 * listContext);

/**
 * \brief Salva o último elemento da lista como corrente.
 * \param list Lista na qual o elemento será procurado.
 * \return Retorna o elemento encontrado.
 * \retval E_OK Em caso de sucesso
 * \retval E_NO_ELEMENTS Não há elementos na lista.
 * \retval OUTRO Em caso de erro.
 */
int list2_lastElement(struct list2 * listContext);

/**
 * \brief Vai para o próximo elemento da lista.
 * \param list Lista na qual o elemento será procurado.
 * \return Retorna o elemento encontrado.
 * \retval E_OK Em caso de sucesso
 * \retval E_NO_MORE_ELEMENTS Não há mais elementos na lista.
 * \retval OUTRO Em caso de erro.
 */
int list2_nextElement(struct list2 * listContext);

/**
 * \brief Vai para o elemento anterior da lista.
 * \param list Lista na qual o elemento será procurado.
 * \return Retorna o elemento encontrado.
 * \retval E_OK Em caso de sucesso
 * \retval E_NO_MORE_ELEMENTS Não há mais elementos na lista.
 * \retval OUTRO Em caso de erro.
 */
int list2_previousElement(struct list2 * listContext);

/**
 * \brief Obtém os dados do elemento atual da lista.
 * \param list Lista da qual o dado será extraído.
 * \return Retorna o dado atual da lista.
 * \retval NULL Em caso de o elemento atual não estar setado.
 */
void * list2_getCurrentElement(struct list2 * listContext);

/**
 * \brief Obtém o tamanho atual da lista.
 * \param list Lista da qual o dado será extraído.
 * \return Retorna o tamanho atual da lista.
 */
int list2_length(struct list2 * listContext);

/**
 * \brief Limpa a lista por inteiro.
 * \param list Lista a ser limpa.
 * \return Retorna um valor de erro ou sucesso.
 * \retval E_OK Sucesso.
 */
int list2_clean(struct list2 * listContext);

int list2_updateCurrentElement(struct list2 * listContext, void * data);


#endif
