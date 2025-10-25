import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

public class App {

    static String nomeArquivoDados;
    static Scanner teclado;
    static Produto[] produtosCadastrados;
    static int quantosProdutos = 0;
    static Fila<Pedido> filaPedidos = new Fila<>();

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("\nDigite enter para continuar...");
        try {
             teclado.nextLine();
         } catch (Exception e) {
            System.err.println("Erro ao processar entrada na pausa: " + e.getMessage());
         }
    }

    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerNumero(String mensagem, Class<T> classe) {
        T valor = null;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensagem + " ");
            String input = teclado.nextLine();
            try {
                if (input == null || input.trim().isEmpty()) {
                    System.out.println("Entrada vazia. Por favor, digite um número.");
                    continue;
                }
                valor = classe.getConstructor(String.class).newInstance(input.trim());
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Formato numérico inválido para " + classe.getSimpleName() + ". Tente novamente.");
            } catch (InvocationTargetException e) {
                System.out.println("Erro ao processar o valor: " + e.getTargetException().getMessage() + " Tente novamente.");
            } catch (NoSuchMethodException e) {
                System.err.println("Erro interno de programação: Classe " + classe.getSimpleName() + " não tem construtor (String).");
                return null;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
                System.out.println("Entrada inválida ou erro interno: " + e.getMessage() + " Tente novamente.");
            }
        }
        return valor;
    }

    static int menu() {
        cabecalho();
        System.out.println("\nMENU:");
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar produto por código");
        System.out.println("3 - Procurar produto por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Finalizar pedido atual");
        System.out.println("6 - Calcular valor médio dos N primeiros pedidos"); // Renumerado
        System.out.println("7 - Filtrar N primeiros pedidos acima de R$ X"); // Renumerado
        System.out.println("8 - Filtrar N primeiros pedidos com produto específico"); // Renumerado
        System.out.println("0 - Sair");

        Integer opcao = lerNumero("Digite sua opção:", Integer.class);
        return (opcao != null) ? opcao : -1;
    }

    static Produto[] lerProdutos(String nomeArquivoDados) {
        Scanner arquivo = null;
        int numProdutos = 0;
        Produto[] produtosCadastrados = null;

        try {
            File file = new File(nomeArquivoDados);
             if (!file.exists()) {
                System.err.println("Erro: Arquivo de produtos '" + nomeArquivoDados + "' não encontrado.");
                return new Produto[0];
             }

            arquivo = new Scanner(file, Charset.forName("UTF-8"));

            if (!arquivo.hasNextLine()) {
                 System.err.println("Erro: Arquivo de produtos está vazio.");
                 return new Produto[0];
             }

            String primeiraLinha = arquivo.nextLine();
            try {
                numProdutos = Integer.parseInt(primeiraLinha);
                if (numProdutos < 0) {
                     System.err.println("Erro: Quantidade de produtos no arquivo (" + numProdutos + ") é inválida.");
                     return new Produto[0];
                 }
            } catch (NumberFormatException e) {
                System.err.println("Erro: A primeira linha do arquivo ('" + primeiraLinha + "') não contém um número válido de produtos.");
                return new Produto[0];
            }

            produtosCadastrados = new Produto[numProdutos];
            int produtosLidos = 0;
            while (arquivo.hasNextLine() && produtosLidos < numProdutos) {
                String linha = arquivo.nextLine();
                if (linha.trim().isEmpty()) continue;
                try {
                    Produto produto = Produto.criarDoTexto(linha);
                    produtosCadastrados[produtosLidos] = produto;
                    produtosLidos++;
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha do produto: '" + linha + "'. Erro: " + e.getMessage());
                }
            }

            quantosProdutos = produtosLidos;

            if (produtosLidos < numProdutos) {
                System.out.println("Aviso: O arquivo indicava " + numProdutos + " produtos, mas apenas " + produtosLidos + " foram lidos ou processados corretamente.");
                Produto[] temp = new Produto[produtosLidos];
                System.arraycopy(produtosCadastrados, 0, temp, 0, produtosLidos);
                produtosCadastrados = temp;
            }

        } catch (IOException excecaoArquivo) {
            System.err.println("Erro de I/O ao ler o arquivo de produtos '" + nomeArquivoDados + "': " + excecaoArquivo.getMessage());
            return new Produto[0];
        } catch (Exception e) {
            System.err.println("Erro inesperado ao ler produtos: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return new Produto[0];
        } finally {
            if (arquivo != null) {
                arquivo.close();
            }
        }
        System.out.println("Carregados " + quantosProdutos + " produtos do arquivo '" + nomeArquivoDados + "'.");
        return produtosCadastrados;
    }


    static Produto localizarProduto() {
        cabecalho();
        System.out.println("\nLOCALIZAR PRODUTO POR CÓDIGO");
        Integer idProduto = lerNumero("Digite o código identificador:", Integer.class);

        if(idProduto == null) return null;

        for (int i = 0; i < quantosProdutos; i++) {
             if (produtosCadastrados[i] != null && produtosCadastrados[i].hashCode() == idProduto) {
                return produtosCadastrados[i];
            }
        }
        return null;
    }

    static Produto localizarProdutoDescricao() {
        System.out.println("\nDigite o nome/descrição do produto:");
        String descricao = teclado.nextLine();

        if (descricao == null || descricao.trim().isEmpty()) {
            System.out.println("Descrição inválida.");
            return null;
        }
        descricao = descricao.trim().toLowerCase();

        for (int i = 0; i < quantosProdutos; i++) {
             if (produtosCadastrados[i] != null && produtosCadastrados[i].descricao != null &&
                 produtosCadastrados[i].descricao.toLowerCase().equals(descricao)) {
                return produtosCadastrados[i];
            }
        }
        return null;
    }

     private static void mostrarProduto(Produto produto) {
        System.out.println("\nDETALHES DO PRODUTO:");
        if (produto != null){
            System.out.println(produto.toString());
        } else {
             System.out.println("--> Produto não localizado!");
        }
    }

    static void listarTodosOsProdutos() {
        System.out.println("\nPRODUTOS CADASTRADOS:");
        if (quantosProdutos == 0) {
            System.out.println("--> Nenhum produto cadastrado.");
            return;
        }
        for (int i = 0; i < quantosProdutos; i++) {
             if (produtosCadastrados[i] != null) {
                 System.out.printf(" %-5d - %-40s - R$ %8.2f%n",
                                   produtosCadastrados[i].idProduto,
                                   produtosCadastrados[i].descricao,
                                   produtosCadastrados[i].valorDeVenda());
             } else {
                 System.out.printf(" [%d] - [Produto inválido/não carregado]%n", i);
             }
        }
    }

    public static Pedido iniciarPedido() {
         cabecalho();
         System.out.println("\nINICIAR NOVO PEDIDO");
        Integer formaPagamento = null;
        while(formaPagamento == null || (formaPagamento != 1 && formaPagamento != 2)) {
             formaPagamento = lerNumero("Forma de pagamento (1=À vista, 2=A prazo):", Integer.class);
             if (formaPagamento == null || (formaPagamento != 1 && formaPagamento != 2)) {
                 System.out.println("Opção inválida. Tente novamente.");
             }
        }

        Pedido novoPedido = new Pedido(LocalDate.now(), formaPagamento);
        String continuar;

        limparTela();
        cabecalho();
        System.out.println("\nADICIONANDO PRODUTOS AO PEDIDO (ID: " + novoPedido.getIdPedido() + ")");
        listarTodosOsProdutos();

        do {
            Produto produto = localizarProdutoDescricao();

            if (produto == null) {
                System.out.println("--> Produto não encontrado.");
            } else {
                if (novoPedido.incluirProduto(produto)) {
                    System.out.println("--> '" + produto.descricao + "' adicionado ao pedido.");
                } else {
                    System.out.println("--> Pedido cheio (Máx: " + Pedido.MAX_PRODUTOS + "). Não foi possível adicionar '" + produto.descricao + "'.");
                    break;
                }
            }
            System.out.print("\nDeseja adicionar outro produto? (S/N): ");
            continuar = teclado.nextLine();

        } while (continuar != null && continuar.trim().equalsIgnoreCase("S"));

         System.out.println("\n--> Inclusão de produtos finalizada para o pedido ID: " + novoPedido.getIdPedido());
        return novoPedido;
    }

    public static void finalizarPedido(Pedido pedidoEmAndamento) {
         cabecalho();
         System.out.println("\nFINALIZAR PEDIDO");
        if (pedidoEmAndamento != null && pedidoEmAndamento.getQuantosProdutos() > 0) {
            filaPedidos.inserir(pedidoEmAndamento);
            System.out.println("--> Pedido ID: " + pedidoEmAndamento.getIdPedido() + " finalizado e adicionado à fila.");
        } else {
            System.out.println("--> Nenhum pedido válido em andamento para finalizar.");
            System.out.println("    Use a opção 4 para iniciar um novo pedido.");
        }
    }


    public static void exibirValorMedioPedidos() {
        cabecalho();
        System.out.println("\nCALCULAR VALOR MÉDIO DOS PRIMEIROS PEDIDOS");

        if (filaPedidos.vazia()) {
            System.out.println("--> A fila de pedidos está vazia.");
            return;
        }

        Integer n = lerNumero("Quantos pedidos (a partir do mais antigo) considerar?", Integer.class);
        if (n == null || n <= 0) {
            System.out.println("Número inválido.");
            return;
        }

        try {
            Function<Pedido, Double> extratorValor = Pedido::valorFinal;
            double media = filaPedidos.calcularValorMedio(extratorValor, n);
            System.out.printf("--> O valor médio dos primeiros %d pedido(s) é: R$ %.2f%n", n, media);
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO: " + e.getMessage());
            System.out.println("   (Atualmente, existem " + filaPedidos.size() + " pedido(s) na fila.)");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado ao calcular a média: " + e.getMessage());
        }
    }

    public static void exibirPedidosAcimaValor() {
        cabecalho();
        System.out.println("\nFILTRAR PEDIDOS ACIMA DE UM VALOR");

        if (filaPedidos.vazia()) {
            System.out.println("--> A fila de pedidos está vazia.");
            return;
        }

        Integer n = lerNumero("Quantos pedidos (a partir do mais antigo) analisar?", Integer.class);
         if (n == null || n < 0) {
            System.out.println("Número inválido.");
            return;
        }


        Double valorMinimo = lerNumero("Qual o valor total mínimo para exibir?", Double.class);
        if (valorMinimo == null || valorMinimo < 0) {
             System.out.println("Valor mínimo inválido.");
            return;
        }


        try {
            Predicate<Pedido> acimaDoValor = pedido -> pedido.valorFinal() > valorMinimo;
            Fila<Pedido> pedidosFiltrados = filaPedidos.filtrar(acimaDoValor, n);

            if (pedidosFiltrados.vazia()) {
                System.out.printf("--> Nenhum dos primeiros %d pedidos possui valor total acima de R$ %.2f%n", n, valorMinimo);
            } else {
                System.out.printf("%n--- Pedidos (dos primeiros %d analisados) com valor acima de R$ %.2f ---%n", n, valorMinimo);
                int count = 1;
                while (!pedidosFiltrados.vazia()) {
                    try {
                         Pedido p = pedidosFiltrados.remover();
                         System.out.println("\n--- PEDIDO FILTRADO " + count++ + " (ID original: " + p.getIdPedido() + ") ---");
                         System.out.println(p.toString());
                         System.out.println("---------------------------------------");
                    } catch (NoSuchElementException ignored) { break; }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERRO: " + e.getMessage());
             System.out.println("   (Atualmente, existem " + filaPedidos.size() + " pedido(s) na fila.)");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado ao filtrar pedidos: " + e.getMessage());
        }
    }

     public static void exibirPedidosComProduto() {
        cabecalho();
        System.out.println("\nFILTRAR PEDIDOS COM PRODUTO ESPECÍFICO");

         if (filaPedidos.vazia()) {
            System.out.println("--> A fila de pedidos está vazia.");
            return;
        }

        Integer n = lerNumero("Quantos pedidos (a partir do mais antigo) analisar?", Integer.class);
         if (n == null || n < 0) {
            System.out.println("Número inválido.");
            return;
        }


        System.out.print("Digite o nome/descrição exato do produto a procurar: ");
        String descProduto = teclado.nextLine();
        if (descProduto == null || descProduto.trim().isEmpty()){
            System.out.println("Nome do produto inválido.");
            return;
        }
        final String descProdutoFinal = descProduto.trim();

        try {
            Predicate<Pedido> contemProduto = pedido -> {
                Produto[] produtosDoPedido = pedido.getProdutos();
                for (int i = 0; i < pedido.getQuantosProdutos(); i++) {
                    if (produtosDoPedido[i] != null && produtosDoPedido[i].descricao != null &&
                        produtosDoPedido[i].descricao.equalsIgnoreCase(descProdutoFinal)) {
                        return true;
                    }
                }
                return false;
            };

            Fila<Pedido> pedidosFiltrados = filaPedidos.filtrar(contemProduto, n);

            if (pedidosFiltrados.vazia()) {
                System.out.println("--> Nenhum dos primeiros " + n + " pedidos contém o produto '" + descProdutoFinal + "'.");
            } else {
                System.out.println("\n--- Pedidos (dos primeiros " + n + " analisados) que contêm '" + descProdutoFinal + "' ---");
                 int count = 1;
                while (!pedidosFiltrados.vazia()) {
                     try {
                         Pedido p = pedidosFiltrados.remover();
                         System.out.println("\n--- PEDIDO FILTRADO " + count++ + " (ID original: " + p.getIdPedido() + ") ---");
                         System.out.println(p.toString());
                         System.out.println("---------------------------------------");
                     } catch (NoSuchElementException ignored) { break; }
                }
            }
        } catch (IllegalArgumentException e) {
             System.out.println("ERRO: " + e.getMessage());
             System.out.println("   (Atualmente, existem " + filaPedidos.size() + " pedido(s) na fila.)");
        } catch (Exception e) {
             System.out.println("Ocorreu um erro inesperado ao filtrar pedidos por produto: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);

         if (produtosCadastrados == null) {
             System.out.println("Erro crítico ao carregar produtos. Encerrando.");
             teclado.close();
             return;
         } else if (quantosProdutos == 0){
              System.out.println("Aviso: Nenhum produto foi carregado. Algumas funcionalidades podem não operar como esperado.");
         }

        Pedido pedidoAtual = null;
        int opcao = -1;

        do {
            limparTela();
            opcao = menu();
            limparTela();

            switch (opcao) {
                case 1:
                    cabecalho();
                    listarTodosOsProdutos();
                    break;
                case 2:
                    cabecalho();
                    mostrarProduto(localizarProduto());
                    break;
                case 3:
                     cabecalho();
                    mostrarProduto(localizarProdutoDescricao());
                    break;
                case 4:
                     if (pedidoAtual != null && pedidoAtual.getQuantosProdutos() > 0) {
                         cabecalho();
                         System.out.println("\nAVISO: Já existe um pedido em andamento.");
                         System.out.println("Finalize-o (opção 5) antes de iniciar um novo.");
                     } else {
                         pedidoAtual = iniciarPedido();
                     }
                    break;
                case 5:
                    finalizarPedido(pedidoAtual);
                    pedidoAtual = null;
                    break;
                case 6: // Renumerado
                    exibirValorMedioPedidos();
                    break;
                case 7: // Renumerado
                    exibirPedidosAcimaValor();
                    break;
                case 8: // Renumerado
                    exibirPedidosComProduto();
                    break;
                case 0:
                    System.out.println("Encerrando o sistema...");
                    break;
                case -1:
                    System.out.println("Erro ao ler a opção do menu.");
                    break;
                default:
                    cabecalho();
                    System.out.println("\nOpção inválida. Por favor, escolha uma opção do menu.");
            }

            if (opcao != 0 && opcao != -1) {
                pausa();
            } else if (opcao == -1) {
                 try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }

        } while(opcao != 0);

        System.out.println("\nObrigado por utilizar o sistema!");
        teclado.close();
    }
}