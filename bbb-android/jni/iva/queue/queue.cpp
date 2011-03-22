#include <CommonLeaks.h>
#include <common.h>
#include <stdio.h>
#include "queue_int.h"
#include "queue.h"
#include <math.h>

queue_memory_t main_memory;
/*
void * queueMonitor(void *){
    list<queue_memoryDesc_t *>::iterator it; //iterador
    queue_memoryDesc_t * m;

    printf("Iniciada a thread monitora da queue.\n");

    
    while(main_memory.monitorRunning){
        pthread_mutex_lock(&main_memory.queue_mutex);    
        for(it=main_memory.busy.begin(); it != main_memory.busy.end(); it++){
            if((getTimestamp() - (*it)->timestamp) > 2000){
                common_logprintf("QUEUE_Monitor - Bloco há %d milissegundos na memória da queue. Size = %d.\n",getTimestamp() - (*it)->timestamp, (*it)->size);
            }
        }
          pthread_mutex_unlock(&main_memory.queue_mutex);
        common_sleep(5000);
    }
  

    return NULL;
};*/

queue_t *queue_create(void)
{
    static int queue_firstTime = 1;

    if (queue_firstTime) {
        pthread_mutex_init(&main_memory.queue_mutex,NULL);
     //   pthread_mutex_lock(&main_memory.queue_mutex);
        main_memory.blocks = 0;
        main_memory.qtMemory = 0;
        main_memory.free.clear();
        main_memory.busy.clear();


        main_memory.memory_area[main_memory.qtMemory] = malloc(QUEUE_MEMORY_SYZE);
        main_memory.qtMemory++;


        queue_memoryDesc_t * m;
        m = (queue_memoryDesc_t *)malloc(sizeof(queue_memoryDesc_t));
        m->initBlock = main_memory.memory_area[main_memory.qtMemory-1];
        m->size = QUEUE_CHUNKS;
        m->enqueue = 0;
        m->busy = 0;
        main_memory.free.push_front(m);

//        pthread_attr_init(&main_memory.attr);
 //       pthread_attr_setdetachstate(&main_memory.attr, PTHREAD_CREATE_JOINABLE);        
     //   main_memory.monitorRunning = 1;
      //  pthread_create(&(main_memory.thread ), &main_memory.attr, queueMonitor, NULL);            

        queue_firstTime = 0;

      //  pthread_mutex_unlock(&main_memory.queue_mutex);
    };

    queue_t *newqueue_p;    
    if (!(newqueue_p = (queue_t*) malloc(sizeof(queue_t)))) {
        return(NULL);
    }

    newqueue_p->firststore_p = NULL;
    newqueue_p->laststore_p = NULL;
    newqueue_p->length = 0;
    newqueue_p->size = sizeof(queue_t);
    newqueue_p->last_timestamp = 0;
    newqueue_p->last_buffersize = 0;
    newqueue_p->firstconsumer_p = NULL;
    newqueue_p->consumers = 0;
    //inicialização das variáveis de controle da concorrência
    pthread_mutex_init(&newqueue_p->mtx, NULL);
    pthread_cond_init(&newqueue_p->condVariable,NULL);

    return (newqueue_p);
}


int queue_destroy(queue_t **queue_pp)
{
   
    // verifica se a queue existe
    if (! queue_pp || ! *queue_pp) {
        return (E_COMMON_NULL_PARAMETER);
    }

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock( &(*queue_pp)->mtx )) {
        return (E_THREAD_MUTEX_LOCK);
    }

    // esvazia a queue se ela nao estiver vazia
    if ((*queue_pp)->length) {
        queue_flushInternal(*queue_pp);
    }

    queue_unregisterAllConsumers(*queue_pp);

    // destroi a queue
    pthread_mutex_destroy( &(*queue_pp)->mtx );

   // main_memory.monitorRunning = 0;    

    //espera a thread finalizar
  //  pthread_join(main_memory.thread, NULL);
    //destrói variável de condição
    pthread_cond_destroy(&((*queue_pp)->condVariable));

    free(*queue_pp);
    *queue_pp = NULL;

    return (E_OK);
}


int queue_broadcast(queue_t * queue){
    pthread_cond_broadcast(&(queue->condVariable));
    return E_OK;
}


queue_consumer_t *queue_registerConsumer(queue_t *queue_p)
{
    queue_consumer_t *p, *newconsumer_p;

    // verifica se a queue existe
    if (!queue_p) {
        return (NULL);
    }

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock  (&queue_p->mtx)) {
        return (NULL);
    }

    // se atingiu o numero maximo de consumidores, retorna
    if (queue_p->consumers == QUEUE_MAX_CONSUMERS) {
        return (NULL);
    }

    if ((newconsumer_p = (queue_consumer_t*)malloc(sizeof(queue_consumer_t)) ) == NULL) {
        return (NULL);
    }

    // inicializacoes
    newconsumer_p->nextconsumer_p = NULL;
    newconsumer_p->queue_p = queue_p;
    newconsumer_p->store_p = queue_p->firststore_p;
    newconsumer_p->hold = 0;
    newconsumer_p->current_timestamp = 0;

    queue_p->consumers++;

    // incrementa os usuarios de toda a queue
    queue_incStoreConsumers(queue_p->firststore_p);

    // insere o usuario na fila de usuarios da queue
    if (p = queue_p->firstconsumer_p) {
        while( p->nextconsumer_p )
            p = p->nextconsumer_p;
        p->nextconsumer_p = newconsumer_p;
    } else {
        queue_p->firstconsumer_p = newconsumer_p;
    }

    // desbloqueia a queue
    pthread_mutex_unlock(&queue_p->mtx);

    return (newconsumer_p);
}


int queue_unregisterConsumer(queue_consumer_t **consumer_pp)
{
    queue_consumer_t *p;
    queue_store_t *store, *storeNext;

    // verifica se o consumidor eh valido    
    if (!*consumer_pp || ! (*consumer_pp)->queue_p->firstconsumer_p) {
        return (E_QUEUE_INVALID_CONSUMER);
    }


    // bloqueia a queue para a operacao
    if (pthread_mutex_lock(&(*consumer_pp)->queue_p->mtx)) {
        return (E_THREAD_MUTEX_LOCK);
    }

    // procura e retira, da fila de usuarios, o usuario *consumer_pp
    if ((*consumer_pp)->queue_p->firstconsumer_p == *consumer_pp) {
        (*consumer_pp)->queue_p->firstconsumer_p = (*consumer_pp)->nextconsumer_p;
    } else {
        p = (*consumer_pp)->queue_p->firstconsumer_p;
        while (p->nextconsumer_p && p->nextconsumer_p!=*consumer_pp) {
            p = p->nextconsumer_p;
        }
        if (p->nextconsumer_p) {
            p->nextconsumer_p = p->nextconsumer_p->nextconsumer_p;
        } else {
            pthread_mutex_unlock(&(*consumer_pp)->queue_p->mtx);
            return(E_QUEUE_INVALID_CONSUMER);
        }
    }
    (*consumer_pp)->queue_p->consumers--;
    // se ainda tem consumidores, apenas decrementa nos elementos que faltavam ser consumidos
    // o numero de consumidores (pois um saiu)
    if ( (*consumer_pp)->queue_p->consumers ) {
        queue_decStoreConsumers( (*consumer_pp)->store_p );


        store = (*consumer_pp)->store_p;
        while (store) {
            storeNext = store->nextstore_p;
            if (store->consumers == 0) {
                queue_freeDirect((*consumer_pp)->queue_p, store);
            }
            store = storeNext;
        }


    // se nao, esvazia a fila (ninguem esta usando)
    } else {
        queue_flushInternal( (*consumer_pp)->queue_p );
    }

    // desbloqueia a queue
    pthread_mutex_unlock(&(*consumer_pp)->queue_p->mtx);

    // limpeza
    free(*consumer_pp);
    *consumer_pp = NULL;

    return (E_OK);
}

// usada internamente para desregistrar todos os consumidores
// de uma fila quando, por exemplo, a fila eh destruida
void queue_unregisterAllConsqueue_memBlock_tumers(queue_t *queue_p)
{
    queue_consumer_t *p;

    while (queue_p->firstconsumer_p) {
        p = queue_p->firstconsumer_p;
        queue_p->firstconsumer_p = queue_p->firstconsumer_p->nextconsumer_p;
        p->queue_p = NULL;
        free(p);
    }
    queue_p->consumers = 0;
}


// usada internamente para incrementar o campo "consumers" de
// cada store da fila, cada vez que um consumidor eh incluido
void queue_incStoreConsumers(queue_store_t *p)
{
    while (p) {
        p->consumers++;
        p = p->nextstore_p;
    }
}


// usada internamente para decrementar o campo "consumers" de
// cada store da fila que nao foi utilizado pelo consumidor
// que esta sendo retirado
void queue_decStoreConsumers(queue_store_t *p)
{
    while (p) {
        p->consumers--;        
        p = p->nextstore_p;
    }
}

int queue_enqueue(queue_t * queue_p, uint8_t * buffer_p, uint32_t buffersize, uint32_t timestamp,
                  QueueExtraData * extraData)
{
    queue_store_t *newstore_p;
    list<queue_memoryDesc_t *>::iterator it;

    // verifica se a queue existe
    if (!queue_p) {
        return(E_COMMON_NULL_PARAMETER);
    }

    // verifica se buffersize eh valido
    //if (buffersize > QUEUE_BLOCK_SIZE) {
        //return(E_ERROR);
    //}

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock(&queue_p->mtx)) {
        return(E_THREAD_MUTEX_LOCK);
    }

    // se nao tem consumidores, nao precisa armazenar
    if (!queue_p->consumers) {
        queue_statFree(buffer_p);
        pthread_mutex_unlock(&queue_p->mtx);
        return (E_OK);
    }

    // cria uma estrutura para armazenar o novo elemento
    if (!(newstore_p = (queue_store_t*) malloc(sizeof(queue_store_t)))) {
        queue_statFree(buffer_p);	
        pthread_mutex_unlock(&queue_p->mtx);

        return (E_COMMON_MEMORY_ERROR);
    }

    newstore_p->buffer_p = buffer_p;
    newstore_p->buffersize = buffersize;
    newstore_p->timestamp = timestamp;

    // cria uma cópia dos dados extra para guardar na queue
    if (extraData) {
        newstore_p->data = extraData->clone();
    } else {
        newstore_p->data = NULL;
    }

    newstore_p->consumers = queue_p->consumers;         // controla o numero de retiradas deste elemento
    newstore_p->prevstore_p = queue_p->laststore_p;
    newstore_p->nextstore_p = NULL;

    // verifica onde inserir
    if (queue_p->length) {                              // nao eh o primeiro
        queue_p->laststore_p->nextstore_p = newstore_p;
        queue_p->laststore_p = newstore_p;
    } else {                                            // eh o primeiro
        queue_p->firststore_p = newstore_p;
        queue_p->laststore_p = newstore_p;
    }

    queue_p->length++;
    queue_p->size += sizeof(queue_store_t)+buffersize;
    queue_p->last_timestamp = timestamp;
    queue_p->last_buffersize = buffersize;

    pthread_mutex_lock(&main_memory.queue_mutex);
    for (it = main_memory.busy.begin(); it != main_memory.busy.end(); it++) {
        if ((*it)->initBlock == buffer_p) {
            (*it)->enqueue = 1;
        }
    }
    pthread_mutex_unlock(&main_memory.queue_mutex);

    // avisa para os que estao ociosos
    queue_updateConsumers(queue_p, newstore_p);
    //libera todos as threads que estão esperando
    pthread_cond_broadcast(&queue_p->condVariable);
    // desbloqueia a queue
    pthread_mutex_unlock(&queue_p->mtx);

    return (E_OK);
}

// esta funcao procura os consumidores de *queue_p que estao sem
// referencia (consumer_p->store_p == NULL) por terem atingido o
// fim da queue, e atualiza este ponteiro para newstore_p
void queue_updateConsumers(queue_t *queue_p, queue_store_t *newstore_p)
{
    queue_consumer_t *consumer_p;

    consumer_p = queue_p->firstconsumer_p;
    while (consumer_p) {
        if (!consumer_p->store_p) {                   // se chegou no fim da queue (NULL),
            consumer_p->store_p = newstore_p;         // pode continuar a partir deste elemento
        }
        consumer_p = consumer_p->nextconsumer_p;
    }
}

int queue_consumerExists(queue_consumer_t * consumer,queue_t * queue){
    int find = 0;
    queue_consumer_t * consumidor;
    consumidor = queue->firstconsumer_p;
    while(consumidor){
        if(consumidor == consumer){
            find = 1;
			break;
		}
        consumidor = consumidor->nextconsumer_p;
    }

    return find;
    
}

int queue_dequeue(queue_consumer_t *consumer_p, uint8_t **buffer_pp, uint32_t *buffersize_p,
                  uint32_t *timestamp_p,  QueueExtraData ** extraData)
{
    // verifica se o usuario exite
    if (!consumer_p) {
        return (E_QUEUE_INVALID_CONSUMER);
    }

    // verifica se a queue do usuario existe
    if (!consumer_p->queue_p) {
        return (E_QUEUE_INVALID_CONSUMER);
    }

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock(&consumer_p->queue_p->mtx)) {
        return (E_THREAD_MUTEX_LOCK);
    }

    // casos em que nao tem nada a fazer
    if (!consumer_p->queue_p->length) {                    // tamanho da queue == 0
        pthread_mutex_unlock(&consumer_p->queue_p->mtx);
        return (E_QUEUE_EMPTY);
    }
    else if (!consumer_p->store_p) {                       // atingiu o fim da fila
        pthread_mutex_unlock(&consumer_p->queue_p->mtx);
        return (E_QUEUE_ENDOF);
    }

    // verifica se o ultimo buffer foi liberado
    if (consumer_p->hold) {                              // se tem um buffer nao liberado (queue_free),
        pthread_mutex_unlock(&consumer_p->queue_p->mtx); // nao pode pegar outro
        return (E_QUEUE_FREE_NEEDED);
    }

    *buffer_pp = consumer_p->store_p->buffer_p;
    *buffersize_p = consumer_p->store_p->buffersize;
    *timestamp_p = consumer_p->store_p->timestamp;
    consumer_p->hold = 1; // aguarda liberacao
    consumer_p->current_timestamp = consumer_p->store_p->timestamp;
    // retorna um ponteiro para os dados extra que estão guardados na queue
    if (extraData) {
        (*extraData) = consumer_p->store_p->data;
    }

    // desbloqueia a queue
    pthread_mutex_unlock(&consumer_p->queue_p->mtx);
    return (E_OK);
}


int queue_dequeueCond(queue_consumer_t *consumer_p, uint8_t **buffer_pp, uint32_t *buffersize_p,
                      uint32_t *timestamp_p,  QueueExtraData ** extraData)
{
    queue_t * queue;
    // verifica se o usuario exite
    if (!consumer_p) {
        return (E_QUEUE_INVALID_CONSUMER);
    }

    // verifica se a queue do usuario existe
    if (!consumer_p->queue_p) {
        return (E_QUEUE_INVALID_CONSUMER);
    }

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock(&consumer_p->queue_p->mtx)) {
        return (E_THREAD_MUTEX_LOCK);
    }
     queue = consumer_p->queue_p;
    // casos em que nao tem nada a fazer
    if (!consumer_p->queue_p->length) {                    // tamanho da queue == 0
        //se não tem nada dorme até que tenha algo       
        pthread_cond_wait(&(queue->condVariable),&(queue->mtx));       
        //if(!queue_consumerExists(consumer_p,queue)){
            pthread_mutex_unlock(&(consumer_p->queue_p->mtx)); // nao pode pegar outro
            return E_QUEUE_EMPTY;
       // };
    }
    else if (!consumer_p->store_p) {                       // atingiu o fim da fila
        pthread_cond_wait(&(queue->condVariable),&(queue->mtx));  
      //  if(!queue_consumerExists(consumer_p,queue)){
            pthread_mutex_unlock(&(consumer_p->queue_p->mtx)); // nao pode pegar outro
            return E_QUEUE_EMPTY;
      //  }
    }

    // verifica se o ultimo buffer foi liberado
    if (consumer_p->hold) {
        // se tem um buffer nao liberado (queue_free),
        pthread_mutex_unlock(&(consumer_p->queue_p->mtx)); // nao pode pegar outro
        NEW_ERROR(E_QUEUE_FREE_NEEDED, "É necessário liberar o buffer anterior da queue");
        return (E_QUEUE_FREE_NEEDED);
    }

    *buffer_pp = consumer_p->store_p->buffer_p;
    *buffersize_p = consumer_p->store_p->buffersize;
    *timestamp_p = consumer_p->store_p->timestamp;
    consumer_p->hold = 1; // aguarda liberacao
    consumer_p->current_timestamp = consumer_p->store_p->timestamp;
    // retorna um ponteiro para os dados extra que estão guardados na queue
    if (extraData) {
        (*extraData) = consumer_p->store_p->data;
    }

    // desbloqueia a queue
    pthread_mutex_unlock(&consumer_p->queue_p->mtx);
    return (E_OK);
}



int queue_flush(queue_consumer_t *consumer_p)
{
    // verifica se a queue existe
    if (!consumer_p->queue_p) {
        return(E_QUEUE_INVALID_CONSUMER);
    }

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock(&consumer_p->queue_p->mtx)) {
        return(E_THREAD_MUTEX_LOCK);
    }

    // se a queue esta vazia, retorna
    if (!consumer_p->queue_p->length) {
        pthread_mutex_unlock(&consumer_p->queue_p->mtx);
        return(E_OK);
    }

    // finalmente, esvazia a queue
    queue_flushInternal(consumer_p->queue_p);

    // desbloqueia a queue
    pthread_mutex_unlock(&consumer_p->queue_p->mtx);

    return(E_OK);
}

// usada internamente para desregistrar todos os consumidores
// de uma fila quando, por exemplo, a fila eh destruida
void queue_unregisterAllConsumers(queue_t *queue_p)
{
    queue_consumer_t *p;

    while (queue_p->firstconsumer_p) {
        p = queue_p->firstconsumer_p;
        queue_p->firstconsumer_p = queue_p->firstconsumer_p->nextconsumer_p;
        p->queue_p = NULL;
        free(p);
    }
    queue_p->consumers = 0;
}


// usado internamente por queue_flush e queue_destroy
void queue_flushInternal(queue_t *queue_p)
{
    if (queue_p->firststore_p != NULL) {
        while (queue_p->firststore_p->nextstore_p) {
            queue_p->firststore_p = queue_p->firststore_p->nextstore_p;
            queue_statFree(queue_p->firststore_p->prevstore_p->buffer_p);
            if (queue_p->firststore_p->prevstore_p->data) {
                delete queue_p->firststore_p->prevstore_p->data;
            }
            free(queue_p->firststore_p->prevstore_p);
        }
        queue_statFree(queue_p->firststore_p->buffer_p);
        if (queue_p->firststore_p->data) {
            delete queue_p->firststore_p->data;
        }
        free(queue_p->firststore_p);
    }

    queue_p->firststore_p = NULL;
    queue_p->laststore_p = NULL;
    queue_p->length = 0;
    queue_p->size = sizeof(queue_t);
}


int queue_length(queue_t *queue_p)
{
    if (queue_p) {
        return (queue_p->length);
    }

    return(0);
}

int queue_lengthCons(queue_consumer_t *temp)
{
    if (temp->queue_p) {
        return (temp->queue_p->length);
    }

    return(0);
}


uint32_t queue_size(queue_t *queue_p)
{
    if (queue_p) {
        return (queue_p->size);
    }

    return(0);
}


uint32_t queue_getLastTimestamp(queue_t *queue_p)
{
    if (queue_p) {
        return (queue_p->last_timestamp);
    }

    return(0);
}


uint32_t queue_getLastBuffersize(queue_t *queue_p)
{
    if (queue_p) {
        return (queue_p->last_buffersize);
    }

    return(0);
}


void *queue_malloc(size_t size)
{
    return (queue_statAlloc(size));
}


void *queue_statAlloc(size_t size)
{
    list<queue_memoryDesc_t *>::iterator it; //iterador
    queue_memoryDesc_t * m;

    //mutex da memória
    pthread_mutex_lock(&main_memory.queue_mutex);
    while (1) {
        //procura por um pedaço de memória livre que seja compatível com o tamanho requisitado
        for (it = main_memory.free.begin(); it != main_memory.free.end(); it++) {
            if ((size_t)(*it)->size >= (size_t)ceil((float)size/QUEUE_BLOCK)) {
                //se encontrou coloca na fila dos em uso e retorna o ponteiro para a região alocada
                m = (queue_memoryDesc_t *)malloc(sizeof(queue_memoryDesc_t));

                m->initBlock = (*it)->initBlock;
                m->size = (int)ceil((float)size/QUEUE_BLOCK);
                m->busy = 1;
                m->enqueue = 0;
//                m->timestamp  = getTimestamp();

                main_memory.blocks += m->size;

                (*it)->initBlock  = (void *)((int)(*it)->initBlock +
                                             ((int)ceil((float)size / QUEUE_BLOCK)* QUEUE_BLOCK));
                (*it)->size -= m->size;

                main_memory.busy.push_front(m);

                pthread_mutex_unlock(&main_memory.queue_mutex);

                return m->initBlock;
            };
        };
        //se não encontrou um pedaço livre e se ainda é possivel alocar mais memória, 
        //aloca e coloca a nova região na lista das memórias livres
        if (main_memory.qtMemory < QUEUE_MAX_BLOCKS) {
            main_memory.memory_area[main_memory.qtMemory] = malloc(QUEUE_MEMORY_SYZE);
            main_memory.qtMemory++;

            queue_memoryDesc_t * m;
            m = (queue_memoryDesc_t *)malloc(sizeof(queue_memoryDesc_t));
            m->initBlock = main_memory.memory_area[main_memory.qtMemory-1];
            m->size = QUEUE_CHUNKS;
            m->enqueue = 0;
            m->busy = 0;
            main_memory.free.push_front(m);
            LogData log;
            log << "Queue: Mais uma porção de memória alocada (" << QUEUE_MEMORY_SYZE/QUEUE_KBYTE << "KB)" << endl;
            log.push();
        } else {
            //se não é mais possível alocar memória, então retorna null e imprime um erro(fatal)
            NEW_ERROR(E_COMMON_MEMORY_ERROR, "Erro ao tentar adicionar dados na queue");
            pthread_mutex_unlock(&main_memory.queue_mutex);
            return NULL;
        }
    };
}


void queue_free(queue_consumer_t *consumer_p)
{
    queue_store_t *prevstore_p;

    // verifica se o consumidor existe
    if (!consumer_p) {
        return;
    }

    // verifica se a queue do consumidor existe
    if (!consumer_p->queue_p) {
        return;
    }

    // bloqueia a queue para a operacao
    if (pthread_mutex_lock(&consumer_p->queue_p->mtx)) {
        return;
    }

    // libera o elemento que estava com o consumidor
    if (consumer_p && consumer_p->hold) {
        consumer_p->hold = 0;              // libera o consumidor para pegar outro buffer
        prevstore_p = consumer_p->store_p;
        consumer_p->store_p = consumer_p->store_p->nextstore_p;
        prevstore_p->consumers--;          // ok, menos um para entregar
        if (prevstore_p->consumers == 0) { // se todos ja foram entregues, pode liberar
            queue_freeDirect( consumer_p->queue_p , prevstore_p );
        }
    }

    // desbloqueia a queue
    pthread_mutex_unlock(&consumer_p->queue_p->mtx);
}


// usada internamente para liberar um elemento da fila que ja
// foi utilizado por todos os consumidores registrados
void queue_freeDirect(queue_t *queue_p, queue_store_t *store_p)
{
    if(store_p->prevstore_p)
        store_p->prevstore_p->nextstore_p = store_p->nextstore_p;
    else
        queue_p->firststore_p = store_p->nextstore_p;

    if(store_p->nextstore_p)
        store_p->nextstore_p->prevstore_p = store_p->prevstore_p;
    else
        queue_p->laststore_p = store_p->prevstore_p;

    queue_p->length--;
    queue_p->size -= sizeof(queue_store_t)+store_p->buffersize;
    if (store_p->data) {
        delete store_p->data;
    }
    if (store_p->buffer_p) {
        queue_statFree(store_p->buffer_p);
    }
    free(store_p);
}

void queue_statFree(void *vp)
{
    if (!vp)
        return;

    list<queue_memoryDesc_t *>::iterator it;
    list<queue_memoryDesc_t *>::iterator retirar;
    list<queue_memoryDesc_t *>::iterator m;
    list<list<queue_memoryDesc_t *>::iterator> tirar;
    list<list<queue_memoryDesc_t *>::iterator>::iterator itTirar;

    int find;
    int juntei;
    tirar.clear();
    pthread_mutex_lock(&main_memory.queue_mutex);
    find = 0;    
    //procura pelo bloco pra desalocar
    for (it = main_memory.busy.begin(); it != main_memory.busy.end(); it++) {
        if ((*it)->initBlock == vp) {
            //se encontrou e estava alocado, seta a flag find. senão retorna
            if ((*it)->busy == 1) {
                retirar = it;
                find = 1;
                main_memory.blocks -= (*it)->size;
                break;
            } else {
                pthread_mutex_unlock(&main_memory.queue_mutex);
                return;
            }
        }
    }
    // se encontrou
    if (find) {
        m = retirar;
        (*m)->busy = 0;
        (*m)->enqueue = 0;
        juntei = 1;
        //procura por blocos adjacentes livre que podem ser unidos, para evitar fragmentação excessiva
        for (it = main_memory.free.begin(); it != main_memory.free.end(); it++) {
            if ((*it)->initBlock == (void *)((int)(*m)->initBlock + (int)((*m)->size * QUEUE_BLOCK))) {
                (*it)->initBlock = (*m)->initBlock;
                (*it)->size += (*m)->size;
                if (juntei) {
                    juntei = 0;
                    m = it;
                } else {
                    tirar.push_back(m);
                };
                } else if((void *)((int)(*it)->initBlock + (int)((*it)->size *QUEUE_BLOCK)) ==
                      (*m)->initBlock) {
                (*it)->size += (*m)->size;
                if (juntei) {
                    juntei = 0;
                    m = it;
                } else {
                    tirar.push_back(m);
                };
            };
         };

        /* Junta pedaçoes adjacentes de memória */
        if (juntei) { //se não juntei
            main_memory.free.push_front((*m));
        } else { //juntei, então encontrei
            free(*retirar);
        };
        //tira os blocos necessários
        for (itTirar = tirar.begin(); itTirar != tirar.end(); itTirar++) {
            free((**itTirar));
            main_memory.free.erase((*itTirar));
        };
        //retira o bloco em uso
        main_memory.busy.erase(retirar);
    } else {
        NEW_WARNING(E_ERROR, "Tentou liberar pedaço de memória não existente");
    }; 
    pthread_mutex_unlock(&main_memory.queue_mutex);
};

void queue_dealloc(void *vp)
{
    list<queue_memoryDesc_t *>::iterator it;
    list<queue_memoryDesc_t *>::iterator retirar;
    list<queue_memoryDesc_t *>::iterator m;
    list<list<queue_memoryDesc_t *>::iterator> tirar;
    list<list<queue_memoryDesc_t *>::iterator>::iterator itTirar;

    int find;
    int juntei;
    tirar.clear();
    find = 0;

    pthread_mutex_lock(&main_memory.queue_mutex);

    for (it = main_memory.busy.begin(); it != main_memory.busy.end(); it++) {
        if ((*it)->initBlock == vp) {
            if ((*it)->busy == 1 && (*it)->enqueue == 0) {
                retirar = it;
                main_memory.blocks -= (*it)->size;
                find = 1;
                break;
            } else {
                return;
            }

        }
    }
    if (find) {
        m = retirar;
        (*m)->busy = 0;
        juntei = 1;
        /* Junta pedaçoes adjacentes de memória */
        for (it = main_memory.free.begin(); it != main_memory.free.end(); it++) {
            if ((*it)->initBlock == (void *)((int)(*m)->initBlock + (int)((*m)->size * QUEUE_BLOCK))) {
                (*it)->initBlock = (*m)->initBlock;
                (*it)->size += (*m)->size;
                if(juntei){
                    juntei = 0;
                    m = it;
                } else {
                    tirar.push_back(m);
                };
            } else if ((void *)((int)(*it)->initBlock + (int)((*it)->size *QUEUE_BLOCK)) ==
                       (*m)->initBlock) {
                (*it)->size += (*m)->size;
                if (juntei) {
                    juntei = 0;
                    m = it;
                } else {
                    tirar.push_back(m);
                };

            };
        };

        /* Junta pedaçoes adjacentes de memória */       
        if (juntei) {
            main_memory.free.push_front((*m));
        } else {
            free(*retirar);
        };
        for (itTirar = tirar.begin(); itTirar != tirar.end(); itTirar++) {
            free((**itTirar));
            main_memory.free.erase((*itTirar));
        };
        main_memory.busy.erase(retirar);

    } else {
        NEW_WARNING(E_ERROR, "Tentou liberar pedaço de memória não existente");
    };
    // printf("Quantidade de chuncks em uso %d e livres %d.\n", main_memory.busy.size(), main_memory.livre.size());
    pthread_mutex_unlock(&main_memory.queue_mutex);
}


int queue_memInUse(void)
{
    return main_memory.blocks * QUEUE_BLOCK;
}


// retorna a memoria total utilizada, em percentual da disponivel
float queue_memPercent(void)
{
    return (float)(main_memory.blocks * QUEUE_BLOCK)/(QUEUE_MEMORY_SYZE);
}

void queue_printMemory()
{
    list<queue_memoryDesc_t *>::iterator it;

    printf("\nMemória em uso:\n");
    for(it = main_memory.busy.begin(); it != main_memory.busy.end(); it++){
        printf("- Inicio = %d, quantidade de blocos = %d.\n",(*it)->initBlock,(*it)->size);
    }
    printf("\nMemória livre:\n");
    for(it = main_memory.free.begin(); it != main_memory.free.end(); it++){
        printf("- Inicio = %d, quantidade de blocos = %d.\n",(*it)->initBlock,(*it)->size);
    }
};


/*int queue_extraData_init(queue_extraData_t *data)
{
    if (!data) {
        return E_NULL_PARAMETER;
    }
    data->data = NULL;
    data->type = COMMON_EXTRA_DATA_UNDEFINED;

    return E_OK;
}


int queue_extraData_destroy(queue_extraData_t *data)
{
    if (!data) {
        return E_NULL_PARAMETER;
    }
    free(data);
    /// \todo Pensar melhor se não precisa destruir os dados internos em data->data
    return E_OK;
}

queue_extraData_t * queue_extraData_create(queue_extraData_t * src)
{
    queue_extraData_t * extra;
    extra = (queue_extraData_t *)malloc(sizeof(queue_extraData_t));
    extra->data = src->data;
    extra->type = src->type;
    return extra;
}*/

void queue_appFinish()
{
    int i = 0;
    list<queue_memoryDesc_t *>::iterator it;
    for(it = main_memory.free.begin(); it != main_memory.free.end(); it++)
        free(*it);
    for(it = main_memory.busy.begin(); it != main_memory.busy.end(); it++)
        free(*it);
    for(i=0; i<main_memory.qtMemory;i++)
        free(main_memory.memory_area[i]);
}

int queue_hasConsumers(queue_t * queue)
{
    if((queue != NULL) && (queue->consumers != 0)){
        return 1;
    }else{
        return 0;
    }
}

