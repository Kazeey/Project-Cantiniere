const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

methods = {

    getMealsByWeekNb : async function(req, res) {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let isException = [];

        let weekNb = req.body.weekNb;

        if (weekNb < 0 && weekNb > 52) {
            res.send(configImport.weekNumberError);
            return false;
        }

        await fetch(baseUrl + 'meal/findallavailableforweek/' + weekNb)
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si l'utilisateur n'est pas trouvé)
                isException.push(this.data.exceptionMessage);
            }
            else
            {
                // Si un utilisateur est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];

                this.mealList = data;
            }
        })

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }

        res.send(this.mealList);
    },

    getMealsForToday: async function(req, res) {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let isException = [];

        await fetch(baseUrl + 'meal/findallavailablefortoday/')
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si l'utilisateur n'est pas trouvé)
                isException.push(this.data.exceptionMessage);
            }
            else
            {
                // Si un utilisateur est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];

                this.mealList = data;
            }
        })

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }

        res.send(this.mealList);
    },

    getAllMeals : async function(req, res) 
    {
        
    },

    getMealById : async function(req, res) 
    {
        
    }
}

exports.data = methods;