import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

    private Celula<E> frente;
    private Celula<E> tras;
    private int tamanho;

    public Fila() {
        Celula<E> sentinela = new Celula<E>();
        frente = sentinela;
        tras = sentinela;
        tamanho = 0;
    }

    public boolean vazia() {
        return frente == tras;
    }

    public void inserir(E item) {
        Celula<E> nova = new Celula<E>(item);
        tras.setProximo(nova);
        tras = nova;
        tamanho++;
    }

    public E remover() {
        if (vazia()) {
            throw new NoSuchElementException("A fila está vazia!");
        }
        Celula<E> celulaRemovida = frente.getProximo();
        if (celulaRemovida == null) {
             throw new NoSuchElementException("Erro interno: frente.getProximo() retornou null em fila não vazia.");
        }
        E itemRemovido = celulaRemovida.getItem();
        frente.setProximo(celulaRemovida.getProximo());

        if (tras == celulaRemovida) {
            tras = frente;
        }

        tamanho--;
        return itemRemovido;
    }

    public E consultarInicio() {
        if (vazia()) {
            throw new NoSuchElementException("A fila está vazia!");
        }
        Celula<E> primeiroItem = frente.getProximo();
         if (primeiroItem == null) {
             throw new NoSuchElementException("Erro interno: fila não vazia mas sem primeiro item após sentinela.");
         }
        return primeiroItem.getItem();
    }

    public int size() {
        return tamanho;
    }

    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser um número positivo.");
        }
        if (quantidade > this.size()) {
            throw new IllegalArgumentException("A fila não contém " + quantidade + " elementos. Tamanho atual: " + this.size());
        }

        double soma = 0.0;
        Celula<E> atual = frente.getProximo();
        int contador = 0;

        while (atual != null && contador < quantidade) {
            Double valor = extrator.apply(atual.getItem());
            if (valor != null) {
                soma += valor;
            }
            atual = atual.getProximo();
            contador++;
        }

        if (contador == 0) return 0.0;
        return soma / contador;
    }

    public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
         if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
         int limiteTeste = Math.min(quantidade, this.size());


        Fila<E> resultado = new Fila<>();
        Celula<E> atual = frente.getProximo();
        int contador = 0;

        while (atual != null && contador < limiteTeste) {
            E item = atual.getItem();
            if (condicional.test(item)) {
                resultado.inserir(item);
            }
            atual = atual.getProximo();
            contador++;
        }

        return resultado;
    }
}