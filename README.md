# Reseller management system

## Introdução

A aplicação tem como objetivo fornecer uma ferramenta de gestão de revendas de veículos, permitindo a administração de usuários, revendas e oportunidades de negócio. A aplicação é desenvolvida utilizando Spring Boot e MongoDB.

## Visão Geral

### Pré-requisitos

- Java 11+
- Spring Boot 2.5+
- MongoDB 4.0+
- Docker
- Maven

### Componentes da Arquitetura

1. **AuthController**: Gerencia autenticação e registro de usuários.
2. **DealershipController**: Gerencia operações relacionadas às revendas.
3. **OpportunityController**: Gerencia operações relacionadas às oportunidades de negócio.
4. **UserController**: Gerencia operações relacionadas aos usuários.

### Configuração e Variáveis de Ambiente

1. Clone o repositório
2. Navegue até o diretório do projeto
3. Configure as variáveis de ambiente:
   - `SPRING_DATA_MONGODB_URI`: URI de conexão com o MongoDB
   - `JWT_SECRET`: Segredo para assinar tokens JWT
   - `SPRING_SECURITY_USER_NAME`: Nome de usuário padrão para autenticação
   - `SPRING_SECURITY_USER_PASSWORD`: Senha de usuário padrão para autenticação

### Docker Compose

Forneça um arquivo `docker-compose.yml` para facilitar a execução da aplicação em um ambiente contêinerizado:

```yaml
version: '3.8'
services:
  app:
    container_name: mobiauto-app
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://root:secret@mongo:27017/mobiauto
      SPRING_SECURITY_USER_NAME: admin
      SPRING_SECURITY_USER_PASSWORD: admin
      JWT_SECRET: your_jwt_secret
    depends_on:
      - mongo
    networks:
      - mobiauto-network

  mongo:
    image: mongo:latest
    container_name: mongo
    environment:
      MONGO_INITDB_DATABASE: mobiauto
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: secret
    ports:
      - "27017:27017"
    networks:
      - mobiauto-network

networks:
  mobiauto-network:
    driver: bridge
```

### Executando a Aplicação

1. Certifique-se de que você tem o Docker e Docker Compose instalados.
2. Navegue até o diretório onde o arquivo `docker-compose.yml` está localizado.
3. Execute o comando:

```bash
docker-compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

## Endpoints

### AuthController

- **POST /api/auth/login**: Autentica um usuário e retorna um token JWT.
- **POST /api/auth/register**: Registra um novo usuário.
- **GET /api/auth/validate**: Valida um token JWT.

### DealershipController

- **GET /api/dealerships**: Recupera todas as revendas.
- **GET /api/dealerships/{id}**: Recupera uma revenda pelo ID.
- **POST /api/dealerships**: Cria uma nova revenda.
- **PUT /api/dealerships/{id}**: Atualiza uma revenda existente.
- **DELETE /api/dealerships/{id}**: Deleta uma revenda pelo ID.

### OpportunityController

- **GET /api/opportunities**: Recupera todas as oportunidades.
- **GET /api/opportunities/{id}**: Recupera uma oportunidade pelo ID.
- **POST /api/opportunities**: Cria uma nova oportunidade.
- **PUT /api/opportunities/{id}**: Atualiza uma oportunidade existente.
- **DELETE /api/opportunities/{id}**: Deleta uma oportunidade pelo ID.
- **PATCH /api/opportunities/{id}/assign**: Atribui uma oportunidade a um assistente.
- **PATCH /api/opportunities/{id}/status**: Atualiza o status de uma oportunidade.
- **POST /api/opportunities/distribute**: Distribui oportunidades não atribuídas entre assistentes.

### UserController

- **GET /api/users/{email}**: Recupera um usuário pelo e-mail.
- **POST /api/users**: Cria um novo usuário.
- **PUT /api/users/{email}**: Atualiza um usuário existente.
- **DELETE /api/users/{email}**: Deleta um usuário pelo e-mail.
- **PATCH /api/users/{email}/role**: Atualiza o papel de um usuário.
- **GET /api/users**: Recupera todos os usuários.
- **GET /api/users/dealership/{dealershipId}**: Recupera usuários por ID da revenda.

## Executando Testes

Para executar os testes, use o comando:

```bash
mvn test
```

## Problemas Comuns

- **Conexão com MongoDB falhou**: Verifique se o MongoDB está em execução e as credenciais estão corretas.
- **Token JWT inválido**: Certifique-se de que o token JWT está sendo gerado corretamente e que o segredo JWT é válido.
