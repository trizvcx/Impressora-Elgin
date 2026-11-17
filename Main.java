import com.sun.jna.Library;
import com.sun.jna.Native;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                Paths.get("E1_Impressora01.dll").toAbsolutePath().toString(), ImpressoraDLL.class); // DLL na raiz do projeto

        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin(int pino, int ti, int tf);

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }
    // Login e senha corretos do programa
    private static final String USUARIO = "admin";
    private static final String SENHA = "1234";
    // Nomes dos arquivos XML de teste (ficam na raiz do projeto)
    private static final String ARQUIVO_XML_SAT = "XMLSAT.xml";
    private static final String ARQUIVO_XML_CANCELAMENTO = "CANC_SAT.xml";
    private static final String ASS_QRCODE = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";
    // true quando a impressora está conectada
    private static boolean conexaoAberta = false;
    // Dados da conexão, preenchidos na opção 1
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;
    // Lê o que o usuário digita no teclado
    private static final Scanner scanner = new Scanner(System.in);

    // mostra a mensagem e devolve o texto digitado
    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    // repete até o usuário digitar um número válido
    private static int lerInteiro(String mensagem) {
        while (true) {
            String texto = capturarEntrada(mensagem);
            try {
                return Integer.parseInt(texto.trim());
            // digitou letras: avisa e pergunta de novo
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite apenas números.");
            }
        }
    }

    // devolve o texto do XML, ou null se não achar o arquivo
    private static String lerArquivo(String nome) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(nome));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Não foi possível ler o arquivo: " + nome);
            return null;
        }
    }

    // Pede usuário e senha, no máximo 3 tentativas
    private static boolean fazerLogin() {
        // até 3 tentativas
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            String login = capturarEntrada("Usuário: ");
            String senha = capturarEntrada("Senha: ");

            // equals compara textos; se acertou, sai com true
            if (login.equals(USUARIO) && senha.equals(SENHA)) {
                System.out.println("Login efetuado!");
                return true;
            }
            // mostra quantas tentativas ainda restam
            int restantes = 3 - tentativa;
            if (restantes > 0) {
                System.out.println("Login incorreto! Tentativas restantes: " + restantes);
            }
        }
        // errou as 3 vezes
        System.out.println("Três tentativas incorretas. Acesso bloqueado.");
        return false;
    }

    // Guarda tipo, modelo, porta e parâmetro da impressora
    public static void configurarConexao() {
        tipo = lerInteiro("Tipo de conexão (1=USB, 2=Serial, 3=Rede, 4=Bluetooth): ");
        modelo = capturarEntrada("Modelo da impressora (i8 ou i9): ");
        conexao = capturarEntrada("Porta de conexão (USB para USB): ");
        parametro = lerInteiro("Parâmetro (0 para USB): ");
        System.out.println("Configuração salva. Agora use a opção 2 para abrir a conexão.");
    }

    public static void abrirConexao() {
        // só abre se ainda não estiver aberta
        if (!conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso.");
            } else {
                System.out.println("Erro ao abrir conexão. Código de erro: " + retorno);
            }
        } else {
            System.out.println("Conexão já está aberta.");
        }
    }

    // Fecha a conexão e marca conexaoAberta como false
    public static void fecharConexao() {
        // se já está fechada, não faz nada
        if (!conexaoAberta) {
            System.out.println("Conexão já está fechada.");
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (retorno == 0) {
            // atualiza a variável de controle
            conexaoAberta = false;
            System.out.println("Conexão fechada com sucesso.");
        } else {
            System.out.println("Erro ao fechar conexão. Código: " + retorno);
        }
    }

    // Imprime o texto informado, repetindo pela quantidade de cópias
    public static void imprimirTexto() {
        // confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // pergunta o que precisa ao usuário
        String texto = capturarEntrada("Texto a imprimir: ");
        int copias = lerInteiro("Quantas cópias? ");
        // se for 0 ou negativo, não imprime nem corta o papel
        if (copias <= 0) {
            System.out.println("Quantidade de cópias inválida. Digite um número maior que zero.");
            return;
        }
        // repete pela quantidade de cópias
        for (int i = 1; i <= copias; i++) {
            // chama o comando e guarda o retorno
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto, 1, 4, 0);
            // ImpressaoTexto só dá erro se for negativo
            if (retorno >= 0) {
                System.out.println("Cópia " + i + " impressa!");
            } else {
                System.out.println("Erro ao imprimir. Código: " + retorno);
            }
        }
        // corta o papel e confere o retorno
        int retornoCorte = ImpressoraDLL.INSTANCE.Corte(5);
        if (retornoCorte != 0) {
            System.out.println("Erro ao cortar o papel. Código: " + retornoCorte);
        }
    }

    // Pergunta o conteúdo e imprime o QR Code
    public static void imprimirQRCode() {
        // confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // pergunta o que precisa ao usuário
        String dados = capturarEntrada("Conteúdo do QR Code: ");
        // chama o comando e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6, 4);
        // confere o retorno (0 = sucesso)
        if (retorno == 0) {
            System.out.println("QR Code impresso com sucesso.");
            // corta o papel e confere o retorno
            int retornoCorte = ImpressoraDLL.INSTANCE.Corte(5);
            if (retornoCorte != 0) {
                System.out.println("Erro ao cortar o papel. Código: " + retornoCorte);
            }
        } else {
            System.out.println("Erro ao imprimir QR Code. Código: " + retorno);
        }
    }

    // Imprime um código de barras CODE 128
    public static void imprimirCodigoBarras() {
        // confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // chama o comando e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, "{A012345678912", 100, 2, 3);
        // confere o retorno (0 = sucesso)
        if (retorno == 0) {
            System.out.println("Código de barras impresso com sucesso.");
            // corta o papel e confere o retorno
            int retornoCorte = ImpressoraDLL.INSTANCE.Corte(5);
            if (retornoCorte != 0) {
                System.out.println("Erro ao cortar o papel. Código: " + retornoCorte);
            }
        } else {
            System.out.println("Erro ao imprimir código de barras. Código: " + retorno);
        }
    }

    // Lê o XMLSAT.xml e imprime o cupom (sem pedir o nome do arquivo)
    public static void imprimirXMLSAT() {
        // 1) confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // 2) mostra qual arquivo será usado e lê o conteúdo
        System.out.println("Usando o arquivo: " + ARQUIVO_XML_SAT);
        String xml = lerArquivo(ARQUIVO_XML_SAT);
        // null = arquivo não encontrado (o aviso já foi mostrado)
        if (xml == null) {
            return;
        }

        // 3) chama o comando e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);
        // 4) retorno 0 = sucesso
        if (retorno == 0) {
            System.out.println("XML SAT impresso com sucesso.");
            // corta o papel e confere o retorno
            int retornoCorte = ImpressoraDLL.INSTANCE.Corte(5);
            if (retornoCorte != 0) {
                System.out.println("Erro ao cortar o papel. Código: " + retornoCorte);
            }
        } else {
            System.out.println("Erro ao imprimir XML SAT. Código: " + retorno);
        }
    }

    // Lê o CANC_SAT.xml e imprime o cancelamento usando a chave ASS_QRCODE
    public static void imprimirXMLCancelamentoSAT() {
        // 1) confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // 2) mostra qual arquivo será usado e lê o conteúdo
        System.out.println("Usando o arquivo: " + ARQUIVO_XML_CANCELAMENTO);
        String xml = lerArquivo(ARQUIVO_XML_CANCELAMENTO);
        // null = arquivo não encontrado (o aviso já foi mostrado)
        if (xml == null) {
            return;
        }

        // 3) chama o comando com a chave ASS_QRCODE e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(xml, ASS_QRCODE, 0);
        // 4) retorno 0 = sucesso
        if (retorno == 0) {
            System.out.println("XML de cancelamento SAT impresso com sucesso.");
            // 5) corta o papel (era isso que faltava)
            int retornoCorte = ImpressoraDLL.INSTANCE.Corte(5);
            if (retornoCorte != 0) {
                System.out.println("Erro ao cortar o papel. Código: " + retornoCorte);
            }
        } else {
            System.out.println("Erro ao imprimir XML de cancelamento SAT. Código: " + retorno);
        }
    }

    // Abre a gaveta de dinheiro
    public static void abrirGaveta() {
        // confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // chama o comando e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);
        // confere o retorno (0 = sucesso)
        if (retorno == 0) {
            System.out.println("Gaveta aberta com sucesso.");
        } else {
            System.out.println("Erro ao abrir gaveta. Código: " + retorno);
        }
    }

    // Pergunta quantos bips e faz a impressora apitar
    public static void sinalSonoro() {
        // confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // pergunta o que precisa ao usuário
        int quantidade = lerInteiro("Quantos bips? ");
        // chama o comando e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(quantidade, 50, 5);
        // confere o retorno (0 = sucesso)
        if (retorno == 0) {
            System.out.println("Bip!");
        } else {
            System.out.println("Erro no sinal sonoro. Código: " + retorno);
        }
    }

    // Pergunta quantas linhas e avança o papel
    public static void avancarPapel() {
        // confere se a conexão está aberta
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }

        // pergunta o que precisa ao usuário
        int linhas = lerInteiro("Quantas linhas avançar? ");
        // chama o comando e guarda o retorno
        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
        // confere o retorno (0 = sucesso)
        if (retorno == 0) {
            System.out.println("Papel avançado com sucesso.");
        } else {
            System.out.println("Erro ao avançar papel. Código: " + retorno);
        }
    }

    // Exibe o menu de opções
    public static void exibirMenu() {
        System.out.println("\n*************************************************");
        System.out.println("**************** MENU IMPRESSORA ****************");
        System.out.println("*************************************************\n");

        System.out.println("1  - Configurar Conexao");
        System.out.println("2  - Abrir Conexao");
        System.out.println("3  - Fechar Conexao");
        System.out.println("4  - Imprimir Texto");
        System.out.println("5  - Imprimir QR Code");
        System.out.println("6  - Imprimir Codigo de Barras");
        System.out.println("7  - Imprimir XML SAT");
        System.out.println("8  - Imprimir XML Cancelamento SAT");
        System.out.println("9  - Abrir Gaveta");
        System.out.println("10 - Sinal Sonoro");
        System.out.println("11 - Avancar Papel");
        System.out.println("0  - Sair");
    }

    public static void main(String[] args) {
        // Se errar o login 3 vezes, encerra sem abrir o menu
        if (!fazerLogin()) {
            scanner.close();
            return;
        }

        // Repete o menu até o usuário escolher 0
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            opcao = lerInteiro("\nDigite a opção desejada: ");

            // cada opção chama um método diferente
            if (opcao == 1) {
                configurarConexao();
            } else if (opcao == 2) {
                abrirConexao();
            } else if (opcao == 3) {
                fecharConexao();
            } else if (opcao == 4) {
                imprimirTexto();
            } else if (opcao == 5) {
                imprimirQRCode();
            } else if (opcao == 6) {
                imprimirCodigoBarras();
            } else if (opcao == 7) {
                imprimirXMLSAT();
            } else if (opcao == 8) {
                imprimirXMLCancelamentoSAT();
            } else if (opcao == 9) {
                abrirGaveta();
            } else if (opcao == 10) {
                sinalSonoro();
            } else if (opcao == 11) {
                avancarPapel();
            } else if (opcao == 0) {
                // fecha a conexão antes de sair
                fecharConexao();
                System.out.println("Programa encerrado.");
            } else {
                // opção que não existe no menu
                System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }
}
