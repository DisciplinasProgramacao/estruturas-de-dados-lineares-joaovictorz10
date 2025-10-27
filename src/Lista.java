import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> {

	private Celula<E> primeiro;
	private Celula<E> ultimo;
	private int tamanho;
	
	/** Cria uma lista vazia com a célula sentinela */
	public Lista() {
		
		Celula<E> sentinela = new Celula<>();
		
		this.primeiro = this.ultimo = sentinela;
		this.tamanho = 0;
	}
	
	 /**
     * Indica se a lista está vazia ou não
     * @return TRUE/FALSE conforme a lista esteja vazia ou não 
     */
	public boolean vazia() {
		
		return (this.primeiro == this.ultimo);
	}
	
    /**
     * Insere um novo elemento na posição indicada. A posição máxima é o tamanho atual da lista e a mínima é 0.
     * @param novo Elemento a ser inserido.
     * @param posicao Posição de referência para inserção.
     * @throws IndexOutOfBoundsException em caso de posição inválida
     */
	public void inserir(E novo, int posicao) {
		
		Celula<E> anterior, novaCelula, proximaCelula;
		
		if ((posicao < 0) || (posicao > this.tamanho))
			throw new IndexOutOfBoundsException("Não foi possível inserir o item na lista: "
					+ "a posição informada é inválida!");
		
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
				
		novaCelula = new Celula<>(novo);
			
		proximaCelula = anterior.getProximo();
			
		anterior.setProximo(novaCelula);
		novaCelula.setProximo(proximaCelula);
			
		if (posicao == this.tamanho)  // a inserção ocorreu na última posição da lista
			this.ultimo = novaCelula;
			
		this.tamanho++;		
	}
	
	/**
     * Remove o elemento da posição indicada. Se a lista estiver vazia ou a posição for inválida (< 0 ou >= tamanho atual da lista),
     * gera exceções. O primeiro elemento da lista é considerado como posição 0.
     * @param posicao Posição do elemento a ser retirado (>= 0 e < tamanho atual da lista)
     * @return Elemento removido da lista
     * @throws IllegalStateException se a lista estiver vazia
     * @throws IndexOutOfBoundsException em caso de posição inválida
     */
	public E remover(int posicao) {
		
		Celula<E> anterior, celulaRemovida, proximaCelula;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		
		if ((posicao < 0) || (posicao >= this.tamanho))
			throw new IndexOutOfBoundsException("Não foi possível remover o item da lista: "
					+ "a posição informada é inválida!");
			
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
				
		celulaRemovida = anterior.getProximo();
				
		proximaCelula = celulaRemovida.getProximo();
				
		anterior.setProximo(proximaCelula);
		celulaRemovida.setProximo(null);
				
		if (celulaRemovida == this.ultimo)
			this.ultimo = anterior;
				
		this.tamanho--;
				
		return (celulaRemovida.getItem());	
	}
	
	 /**
     * Retorna, sem remover, o elemento da posição indicada pelo parâmetro. A primeira posição da lista é 
     * considerada a posição 0 e, assim, a última é (tamanho - 1). Lança exceções para lista vazia ou posições inválidas.
     * @param posicao Posição do elemento a ser consultado (>= 0 e < tamanho atual da lista)
     * @return O elemento da posição indicada (consulta sem remoção)
     * @throws IllegalStateException se a lista estiver vazia
     * @throws IndexOutOfBoundsException em caso de posição inválida 
     */
  public E obterElemento(int posicao) {
    
    	if (vazia())
    		throw new IllegalStateException("Não foi possível obter o item da lista: "
    				+ "a lista está vazia!");
    	
    	if ((posicao < 0) || (posicao >= this.tamanho))
    		throw new IndexOutOfBoundsException("Não foi possível obter o item da lista: "
    				+ "a posição informada é inválida!");
    	
    	// Inicia a partir do primeiro (sentinela), e a primeira célula com item está em .getProximo()
    	Celula<E> atual = this.primeiro;
    	
    	// Percorre até a posição, contando a partir do .getProximo() do sentinela como posição 0
    	for (int i = 0; i <= posicao; i++) 
    		atual = atual.getProximo();
    			
    	return atual.getItem();
    }
    
    /**
     * Conta quantos elementos da lista atendem à condição estabelecida pelo predicado.
     * @param condicional Predicado com a condição para verificação de elementos da lista.
     * @return inteiro com a quantidade de elementos que atendem ao predicado (inteiro não-negativo)
     */
 public int contar(Predicate<E> condicional){
        
    	int contagem = 0;
    	Celula<E> atual = this.primeiro.getProximo(); // Começa na primeira célula com item
    	
    	while (atual != null) {
    		if (condicional.test(atual.getItem())) {
    			contagem++;
    		}
    		atual = atual.getProximo();
    	}
    	return contagem;
    }
    
    /**
   	 * Calcula e retorna a soma de um determinado atributo dos elementos da lista,
   	 * utilizando uma função de extração fornecida.
   	 * @param extrator uma função que extrai um valor numérico (Double) de cada elemento da lista.
   	 * @return a soma do atributo extraído dos elementos.
   	 */
  public double obterSoma(Function<E, Double> extrator) {
   	
   		double soma = 0.0;
    	Celula<E> atual = this.primeiro.getProximo(); // Começa na primeira célula com item
    	
    	while (atual != null) {
    		soma += extrator.apply(atual.getItem());
    		atual = atual.getProximo();
    	}
   		return soma;
   	}
    
   	/**
     * Retorna a quantidade atual de elementos na lista.
     * @return Inteiro não negativo com a quantidade atual de elementos na lista.
     */
    public int tamanho(){
        return tamanho;
    }
    
    /**
     * Retorna uma string com informação detalhada de cada elemento da lista. 
     * A string indica as posições dos elementos, iniciando-se em 0.
     * @return Uma string com as informações de cada elemento da lista
     */
    @Override
	public String toString() {
    	
		Celula<E> aux;
		StringBuilder listaString = new StringBuilder("A lista está vazia!");
		
	    if (!vazia()) {
            int contador = 0;
			aux = primeiro.getProximo();
			while (aux != null) {
                String dado = String.format("Posição %d: %s\n", contador, aux.getItem().toString());
				listaString.append(dado);
				aux = aux.getProximo();
                contador++;
			}
		} 	
        return listaString.toString();
	}
}