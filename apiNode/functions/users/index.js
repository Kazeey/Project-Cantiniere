let graphqlHTTP = require('express-graphql');
let buildSchema = require('graphql');
let fetch = require('node-fetch');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
let emptyValue = "Non renseigné(e)";

let arrayDataUsers = [];
let arrayImgUsers = [];
let listUsers = [];

let role = "client";
let sexe;

const isLunchLadyValue = (vanillaValue) => {    
    if (vanillaValue == true) // Si le rôle de l'utilisateur vaut true, alors c'est la cantinière
    {
        return "Lunch Lady"
    }
    else                      // Sinon c'est un client
    {
        return "Client"
    }
}

const aliasSex = (vanillaValue) => {
    switch(vanillaValue) // En fonction de la valeur du paramètre
    {
        case 0 : 
            return "Homme";
        case 1 : 
            return "Femme";
        case 2 :
            return "Autre";
    }
}

const isUndefined = (vanillaValue) => {
    if(vanillaValue == undefined)
    {
        return emptyValue;
    }
    else
    {
        return vanillaValue;
    }
}

methods = {
    getAllUsers : async function(req, res) {

        // Récupère chaque utilisateur de la base
        await fetch(baseUrl + "user/findall")
        .then(response => response.json())
        .then(data => {
            arrayDataUsers.push(data); // Insère toutes les données dans le tableau déclaré plus haut
        })
        
        // Pour rajouter l'image au profil
        for(let i = 0; i < arrayDataUsers[0].length; i++) // Pour chaque utilisateur
        {            
            let currentUser  = arrayDataUsers[0][i];

            let userId  = currentUser.id;
            let phone   = currentUser.phone;
            let address = currentUser.address;
            let postalCode = currentUser.postalCode;
            let town    = currentUser.town;

            role = isLunchLadyValue(currentUser.isLunchLady); // Récupère le rôle de l'utilisateur

            sexe = aliasSex(currentUser.sex); // Récupère le sexe de l'utilisateur            

            // Vérification des valeurs pouvant être undefined
            phone = isUndefined(phone);
            address = isUndefined(address);
            postalCode = isUndefined(postalCode);
            town = isUndefined(town);

            await fetch(baseUrl + "user/findimg/" + userId) // Récupère l'image qui lui est associée
            .then(response => response.json())
            .then(data => {
                arrayImgUsers.push(data); // Insère les données dans le tableau déclaré plus haut
            })

            let currentImage = arrayImgUsers[0];
            let imageId = arrayImgUsers[0].id;

            // Création d'un objet pour chaque personne, avec toutes les informations nécéssaires
            let person = {
                id          : userId,                     // Id de l'utilisateur, on s'en sert comme token 
                name        : currentUser.name,         
                firstname   : currentUser.firstname,
                sex         : sexe,                 
                mail        : currentUser.email,
                role        : role,
                wallet      : currentUser.wallet,
                address     : address,
                town        : town,
                postalCode  : postalCode,
                phone       : phone,
                status      : currentUser.status,
                image : {                                 // Contient toutes les informations pour l'image de l'utilisateur
                    id          : imageId,
                    imagePath   : currentImage.imagePath,
                    image64     : currentImage.image64
                }
            }

            listUsers.push(person);
        }
        
        res.send(listUsers);
    }, 

    getUserById : async function(req, res) {
        userIdToFind = req.body.userId; // Possiblement changer par un req.query.userId une fois qu'on aura passé le paramètre par POST et non GET

        // TODO : Faire un input qui permet d'envoyer ça en post
        console.log(userIdToFind)
    }
}

exports.data = methods;
