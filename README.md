# Integração Java com Impressora Elgin i8/i9

Programa em Java para controlar uma impressora térmica Elgin (i8 ou i9) pela DLL `E1_Impressora01.dll`, usando JNA.

## O que o programa faz

Após o login, o menu permite:

- configurar e abrir/fechar a conexão com a impressora
- imprimir texto, QR Code e código de barras
- imprimir XML SAT e XML de cancelamento SAT
- abrir a gaveta, emitir sinal sonoro e avançar o papel

## Login de teste

- **Usuário:** `admin`
- **Senha:** `1234`
- São permitidas no máximo **3 tentativas**.

## Como executar (IntelliJ IDEA)

1. Abra a pasta do projeto (a que contém `Main.java` e `E1_Impressora01.dll`).
2. Configure o JDK (11 ou superior): *File → Project Structure → Project → SDK*.
3. Adicione o JNA: *File → Project Structure → Libraries → + → Java* e escolha `libs Java/jna-5.15.0.jar`.
4. Confira o *Working directory* da execução: deve ser a pasta do projeto (onde está a DLL).
5. Ligue a impressora, conecte o cabo USB e execute `Main.java`.

Valores típicos de teste USB: tipo `1`, modelo `i9` (ou `i8`), porta `USB`, parâmetro `0`.

O Java e a DLL precisam ter a mesma arquitetura (64 bits na raiz; 32 bits em `Bibliotecas E1 Impressora/x86` se o Java for 32 bits).

## Opções do menu

| Opção | Função |
|------:|--------|
| 1 | Configurar conexão |
| 2 | Abrir conexão |
| 3 | Fechar conexão |
| 4 | Imprimir texto |
| 5 | Imprimir QR Code |
| 6 | Imprimir código de barras |
| 7 | Imprimir XML SAT (`XMLSAT.xml`) |
| 8 | Imprimir XML cancelamento SAT (`CANC_SAT.xml`) |
| 9 | Abrir gaveta |
| 10 | Sinal sonoro |
| 11 | Avançar papel |
| 0 | Sair (fecha a conexão) |

## Tecnologias

- Java 11+
- JNA 5.15.0
- SDK Elgin (`E1_Impressora01.dll`)

## Autores

*(preencher em ordem alfabética)*

- Ana Beatriz Souza
- Carlos Henrique Pereira
- Marcos Vinicius Almeida
