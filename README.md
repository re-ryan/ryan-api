# ryan-api

Projeto acadêmico Biblioteca Fácil, desenvolvido em Java 21 com Spring Boot e Maven.

## Contexto

O Biblioteca Fácil é uma plataforma digital voltada para a busca e solicitação de reserva de livros físicos em 
bibliotecas parceiras. 
A proposta do sistema é permitir que o usuário pesquise um livro em uma base centralizada e descubra em quais 
bibliotecas aquele livro existe. Quando a informação estiver disponível, o sistema também poderá mostrar dados 
de estoque ou disponibilidade e permitir que o usuário solicite uma reserva para retirada presencial. 
O sistema não substitui a operação interna das bibliotecas. Ele funciona como uma camada de descoberta, 
consulta e intermediação entre leitores e bibliotecas.

## Etapa 3 - API REST

Nesta etapa, a aplicação utiliza a arquitetura `Cliente HTTP -> Controller -> Service -> Map`.

Para iniciar a API:

```shell
mvn spring-boot:run
```

Os recursos principais estão disponíveis em:

- `/api/livros`
- `/api/autores`
- `/api/categorias`
- `/api/bibliotecas`
- `/api/usuarios`

A documentação pode ser consultada em `http://localhost:8080/swagger-ui.html` e a especificação OpenAPI em
`http://localhost:8080/v3/api-docs`.

A coleção de demonstração está em `postman/Biblioteca-Facil-Etapa-3.postman_collection.json`.
