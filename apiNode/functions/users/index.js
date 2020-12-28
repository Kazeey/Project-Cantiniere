const fetch = require('node-fetch');
const configImport = require('../config');

const baseUrl = 'http://192.168.1.74:8080/lunchtime/';

const emptyValue = "Non renseigné(e)";

/* 
*   Si le rôle de l'utilisateur vaut true, alors c'est la cantinière
*   Sinon c'est un client
*/    
const isLunchLadyValue = (vanillaValue) => {   

    if (vanillaValue == true)
    {
        return "Lunch Lady"
    }
    else                     
    {
        return "Client"
    }
}
 
// En fonction de la valeur du paramètre retourné depuis la base
const aliasSex = (vanillaValue) => {
    switch(vanillaValue)
    {
        case 0 : 
            return "Homme";
        case 1 : 
            return "Femme";
        case 2 :
            return "Autre";
    }
}

// Vérifie si le paramètre est "undefined" ou non, si oui retourne "Non renseigné(e)" sinon retourne la valeur
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
    getAllUsers : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let arrayDataUsers = [];
        let arrayImgUsers = [];
        
        let listUsers = [], currentUser;

        let role = "client";
        let sexe;

        // Récupère chaque utilisateur de la base
        await fetch(baseUrl + "user/findall")
        .then(response => response.json())
        .then(data => {
            // Insère toutes les données dans le tableau déclaré plus haut
            arrayDataUsers.push(data); 
        })

        // Pour rajouter l'image au profil
        for(let i = 0; i < arrayDataUsers[0].length; i++) // Pour chaque utilisateur
        {            
            currentUser  = arrayDataUsers[0][i];

            let userId  = currentUser.id;
            let phone   = currentUser.phone;
            let address = currentUser.address;
            let postalCode = currentUser.postalCode;
            let town    = currentUser.town;

            role = isLunchLadyValue(currentUser.isLunchLady); // Récupère le rôle de l'utilisateur
            sexe = aliasSex(currentUser.sex); // Récupère le sexe de l'utilisateur            

            // Vérification des valeurs pouvant être "undefined"
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
                email        : currentUser.email,
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

    getUserById : async function(req, res) 
    {
        // TODO : Possiblement passer sur un envoi de 2 tableaux, comme pour le login
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let arrayDataUsers = [];
        let arrayImgUsers = [];
        
        let isException = [];
        let user = [];

        let role = "client";
        let sexe;

        // Récupération du paramètre userId passe en POST
        userIdToFind = req.query.userId; 

        // Récupère les données de l'utilisateur ciblé
        await fetch(baseUrl +  "user/find/" + userIdToFind)
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si l'utilisateur n'est pas trouvé)
                isException.push(data.exceptionMessage);
            }
            else
            {
                // Si un utilisateur est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];
                // Insère toutes les données dans le tableau déclaré plus haut
                arrayDataUsers.push(data); 
            }
        })

        if(isException.length != 0)
        {
            res.send("Aucun utilisateur trouvé pour cet identifiant.");
            return false; 
        }

        userToFind = arrayDataUsers[0];

        let userId  = userToFind.id;
        let phone   = userToFind.phone;
        let address = userToFind.address;
        let postalCode = userToFind.postalCode;
        let town    = userToFind.town;

        role = isLunchLadyValue(userToFind.isLunchLady); // Récupère le rôle de l'utilisateur
        sexe = aliasSex(userToFind.sex); // Récupère le sexe de l'utilisateur            

        // Vérification des valeurs pouvant être "undefined"
        phone = isUndefined(phone);
        address = isUndefined(address);
        postalCode = isUndefined(postalCode);
        town = isUndefined(town);

        await fetch(baseUrl + "user/findimg/" + userIdToFind) // Récupère l'image qui lui est associée
        .then(response => response.json())
        .then(data => {
            arrayImgUsers.push(data); // Insère les données dans le tableau déclaré plus haut
        })

        let currentImage = arrayImgUsers[0];
        let imageId = arrayImgUsers[0].id;

        // Création d'un objet pour chaque personne, avec toutes les informations nécéssaires
        let person = {
            id          : userId,                     // Id de l'utilisateur, on s'en sert comme token 
            name        : userToFind.name,         
            firstname   : userToFind.firstname,
            sex         : sexe,                 
            email        : userToFind.email,
            role        : role,
            wallet      : userToFind.wallet,
            address     : address,
            town        : town,
            postalCode  : postalCode,
            phone       : phone,
            status      : userToFind.status,
            image : {                                 // Contient toutes les informations pour l'image de l'utilisateur
                id          : imageId,
                imagePath   : currentImage.imagePath,
                image64     : currentImage.image64
            }
        }
        
        user.push(person);
        
        res.send(user);
    }
}

exports.data = methods;
