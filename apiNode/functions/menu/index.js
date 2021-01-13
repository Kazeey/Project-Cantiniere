const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';

let con = configImport.connexionSQL;
let messageError = configImport.messageError;
let userError = configImport.userError;
let actionMessage = configImport.actionMessage;


methods = {
    getMenuForWeek : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let isException = [];

        weekNb = req.body.weeknumber;

        if (weekNb < 0 && weekNb > 52) {
            res.send(configImport.weekNumberError);
            return false;
        }

        await fetch(baseUrl + 'menu/findallavailableforweek/' + weekNb)
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

                this.menuList = data;
            }
        })

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }

        res.send(this.menuList);
    },

    addMenu : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.put({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'menu/add',
            body: JSON.stringify(req.body.menu)
        }, 
            function(error, response, body) {
                res.send(JSON.stringify(actionMessage))
            }
        );
    },

    updateMenu : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.patch({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'menu/update/' + JSON.stringify(req.body.menuId),
            body: JSON.stringify(req.body.menu)
        },
            function(error, response, body) {
                res.send(JSON.stringify(actionMessage));
            }
        );
    },

    getMenuById : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        await fetch(baseUrl + 'menu/find/' + req.body.menuId)
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

                this.menu = data;
            }
        })

        res.send(this.menu);

        /*request.get({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'menu/find/' + JSON.stringify(req.body.menuId),
        },
            function(error, response, body) {
                menu = response.body;
                console.log(typeof menu, menu)
                res.send(response);
            }
        );*/
    },

    deleteMenu : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        request.delete({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + 'menu/delete/' + JSON.stringify(req.body.menuId)
        },
            function(error, response, body) {
                res.send(JSON.stringify(actionMessage));
            }
        );
    },

    getAllMenus : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    },

    updateMenuImage : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    },

    getMenuImage : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    },

    getMenuForToday : async function(req, res) 
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    }
    
}

exports.data = methods;