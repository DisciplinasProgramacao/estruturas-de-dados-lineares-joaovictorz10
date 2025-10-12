import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * Retorna a quantidade de itens armazenados na pilha.
	 * * @return um inteiro representando o número de itens na pilha.
	 */
	public int size() {
		int contador = 0;
		Celula<E> atual = topo;
		while (atual != fundo) {
			contador++;
			atual = atual.getProximo();
		}
		return contador;
	}

	/**
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual.
	 * * Os elementos são mantidos na mesma ordem em que estavam na pilha original.
	 * A pilha original não é modificada.
	 * Caso a pilha atual possua menos elementos do que o valor especificado,
	 * uma exceção será lançada.
	 *
	 * @param numItens o número de itens a serem copiados da pilha original. Deve ser um valor não negativo.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se a pilha não contém numItens elementos ou se numItens for negativo.
	 */
	public Pilha<E> subPilha(int numItens) {
		
		if (numItens < 0) {
			throw new IllegalArgumentException("O número de itens não pode ser negativo.");
		}

		if (this.size() < numItens) {
			throw new IllegalArgumentException("A pilha não contém " + numItens + " elementos.");
		}

		Pilha<E> sub = new Pilha<>();
		Pilha<E> auxiliar = new Pilha<>();
		Celula<E> atual = this.topo;

		// 1. Lê os N itens do topo e os insere em uma pilha auxiliar (invertendo a ordem)
		for (int i = 0; i < numItens; i++) {
			auxiliar.empilhar(atual.getItem());
			atual = atual.getProximo();
		}

		// 2. Desempilha da auxiliar e empilha na nova pilha (restaurando a ordem original)
		while (!auxiliar.vazia()) {
			sub.empilhar(auxiliar.desempilhar());
		}

		return sub;
	}
}