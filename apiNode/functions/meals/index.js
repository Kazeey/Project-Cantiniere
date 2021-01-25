const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

let con = configImport.connexionSQL;
let messageError = configImport.messageError;
let userError = configImport.userError;
let actionMessage = configImport.actionMessage;

methods = {

    addMeal : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.put({
            headers: {'content-type' : 'application/json'},
            url:     baseUrl + 'meal/add',
            body:    JSON.stringify(req.body.meal)
        }, function(error, response, body){
            console.log(body);
        });

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }

        res.send(this.meal);
    },

    updateMeal : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.patch({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'meal/update/' + JSON.stringify(req.body.mealId),
            body: JSON.stringify(req.body.meal)
        },
            function(error, response, body) {
                res.send(JSON.stringify(actionMessage));
            }
        );
    },

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

    getImage: async function(req, res) {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let isException = [];

        let id = req.body.id;

        await fetch(baseUrl + 'meal/findimg/' + id)
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

                this.image = data;
            }
        })

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }

        res.send(this.image);
    },

    getAllMeals : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        await fetch(baseUrl + 'meal/findall')
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

    deleteMeal : async function(res, res) {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.delete({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'meal/delete/' + JSON.stringify(req.body.mealId)
        },
            function(error, response, body) {
                res.send(JSON.stringify(actionMessage));
            }
        );
    },

    getMealById : async function(req, res) 
    {
        
    }
}

exports.data = methods;