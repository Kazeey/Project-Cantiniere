const fetch = require('node-fetch');
const configImport = require('../config');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

methods = {
    getOrderByUserToday : async function(req, res) 
    {
        let login = [];
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    },

    getOrderByUser : async function(req, res){
        // Récupération du paramètre userId passe en POST
        userIdToFind = req.query.userId; 

        let orderList;
        let isException = [];

        // Récupère les données de l'utilisateur ciblé
        await fetch(baseUrl +  "order/findallforuser/" + userIdToFind)
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si une commande n'est pas trouvée)
                isException.push(this.data.exceptionMessage);
            }
            else
            {
                // Si une commande est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];

                this.orderList = data;
            }
        })

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }

        res.send(this.orderList);

    }
}

exports.data = methods;