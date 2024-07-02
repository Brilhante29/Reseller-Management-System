// Função para gerar UUID v4
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// Inicializa o banco de dados mobiauto_db
db = db.getSiblingDB('mobiauto_db');

// Cria as coleções
db.createCollection('dealerships');
db.createCollection('opportunities');
db.createCollection('users');

// Gera IDs UUID v4
const user1_id = generateUUID();
const user2_id = generateUUID();
const dealership1_id = generateUUID();
const dealership2_id = generateUUID();
const opportunity1_id = generateUUID();
const opportunity2_id = generateUUID();

// Insere documentos na coleção users
db.users.insertMany([
    {
        "_id": user1_id,
        "email": "user1@example.com",
        "name": "User One",
        "password": "password1",
        "role": "ADMIN",
        "dealership": dealership1_id
    },
    {
        "_id": user2_id,
        "email": "user2@example.com",
        "name": "User Two",
        "password": "password2",
        "role": "USER",
        "dealership": dealership2_id
    }
]);

// Insere documentos na coleção dealerships
db.dealerships.insertMany([
    {
        "_id": dealership1_id,
        "cnpj": "12345678901234",
        "corporateName": "Dealership One",
        "users": [user1_id]
    },
    {
        "_id": dealership2_id,
        "cnpj": "23456789012345",
        "corporateName": "Dealership Two",
        "users": [user2_id]
    }
]);

// Insere documentos na coleção opportunities
db.opportunities.insertMany([
    {
        "_id": opportunity1_id,
        "name": "Opportunity One",
        "email": "opportunity1@example.com",
        "phone": "1234567890",
        "brand": "Brand One",
        "model": "Model One",
        "version": "Version One",
        "yearModel": 2020,
        "status": "NEW",
        "conclusionReason": null,
        "assignedDate": new Date(),
        "conclusionDate": null,
        "user": user1_id
    },
    {
        "_id": opportunity2_id,
        "name": "Opportunity Two",
        "email": "opportunity2@example.com",
        "phone": "0987654321",
        "brand": "Brand Two",
        "model": "Model Two",
        "version": "Version Two",
        "yearModel": 2021,
        "status": "IN_PROGRESS",
        "conclusionReason": null,
        "assignedDate": new Date(),
        "conclusionDate": null,
        "user": user2_id
    }
]);
