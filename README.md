# Biblioteca Fácil

Projeto acadêmico desenvolvido em Java 21 com Spring Boot, Spring Data JPA, Spring Cloud OpenFeign e Maven para a pós-graduação do Infnet.

## Contexto

O Biblioteca Fácil é uma plataforma voltada à busca e à reserva de livros físicos em bibliotecas parceiras. A aplicação mantém um catálogo centralizado e permite consultar livros, autores, categorias, bibliotecas e usuários.

O sistema funciona como uma camada de descoberta e intermediação. Ele não substitui os sistemas internos de empréstimos das bibliotecas.

## Arquitetura

A aplicação segue o fluxo:

~~~text
Cliente HTTP → Controller → Service → Repository → H2
                                └→ OpenFeign → BrasilAPI
~~~

- **Controller:** recebe as requisições HTTP e delega as operações.
- **Service:** concentra validações, regras da aplicação e limites transacionais.
- **Repository:** utiliza Spring Data JPA para acessar o banco.
- **H2:** mantém os dados em memória durante a execução.
- **OpenFeign:** realiza a comunicação HTTP declarativa com a BrasilAPI.

## Requisitos

- Java 21;
- Maven.

## Execução

Na raiz do projeto, execute:

~~~bash
mvn spring-boot:run
~~~

A API ficará disponível em:

~~~text
http://localhost:8080
~~~

O banco H2 é criado em memória quando a aplicação inicia e removido quando ela é encerrada. A aplicação também cadastra dados de demonstração durante a inicialização.

Além das validações da API, o schema protege os campos obrigatórios e os identificadores de negócio que precisam ser únicos.

## Documentação da API

Com a aplicação em execução:

- Swagger UI: **http://localhost:8080/swagger-ui.html**
- OpenAPI JSON: **http://localhost:8080/v3/api-docs**

Os recursos principais são:

- **/api/autores**
- **/api/categorias**
- **/api/livros**
- **/api/bibliotecas**
- **/api/usuarios**

Os cinco recursos persistentes disponibilizam inclusão, alteração, exclusão, listagem e obtenção por identificador. Os identificadores são gerados automaticamente pelo banco e não devem ser enviados nos corpos de POST.

## Integração de livros por ISBN

No cadastro de um livro, a aplicação utiliza Spring Cloud OpenFeign para consultar o ISBN-13 na BrasilAPI:

~~~http
POST /api/livros
~~~

Quando o ISBN é encontrado, título, editora, ano de publicação, descrição e URL da capa retornados pelo serviço externo substituem os respectivos dados da requisição. Campos ausentes na resposta externa preservam os valores informados pelo cliente.

O ISBN e a edição são sempre mantidos conforme a requisição. Autores e categorias também não são alterados pela consulta, pois são relacionamentos administrados separadamente pela aplicação.

Se o ISBN não for encontrado ou a BrasilAPI estiver indisponível, o cadastro continua normalmente com os dados recebidos. A URL do serviço e os tempos máximos de conexão e leitura estão configurados em `application.properties`. Alterações de livros continuam manuais e não realizam uma nova consulta externa.

## Busca e ordenação

As buscas aceitam o parâmetro **sort** do Spring Data. Sem esse parâmetro, os resultados são ordenados de forma ascendente pelo campo principal do recurso.

Exemplos:

~~~http
GET /api/livros/busca?titulo=estrela
GET /api/livros/busca?titulo=a&sort=titulo,desc
GET /api/autores/busca?nome=assis&sort=nome,asc
~~~

Os campos padrão são:

| Recurso | Campo de busca | Ordenação padrão |
|---|---|---|
| Autor | nome | nome,asc |
| Categoria | nome | nome,asc |
| Livro | titulo | titulo,asc |
| Biblioteca | nome | nome,asc |
| Usuário | nome | nomeCompleto,asc |

## Testes

Para executar a suíte automatizada:

~~~bash
mvn test
~~~

A coleção de requisições para validação manual está em:

~~~text
postman/Biblioteca-Facil-Etapa-4.postman_collection.json
~~~

Importe a coleção no Postman, inicie a aplicação e execute as requisições na ordem apresentada. Os scripts da coleção armazenam os identificadores gerados nos cadastros e os reutilizam nas operações seguintes.

## Evolução acadêmica

O histórico do repositório preserva os marcos:

- **etapa-1:** modelo orientado a objetos;
- **etapa-2:** Collections, Services e armazenamento em memória;
- **etapa-3:** API REST com Spring Boot;
- **etapa-4:** versão final com Spring Data JPA, criada somente ao término da etapa.
