let methods = {
    menusAdmin : [
        {
            name : "Commandes du jour",
            path : "/dailyOrder"
        },
        {
            name : "Historique des commandes",
            path : "/historic"
        },
        {
            name : "Gestion des menus",
            path : "/manageMenu"
        },
        {
            name : "Gestion des aliments",
            path : "/manageProduct"
        },
        {
            name : "Gestion des utilisateurs",
            path : "/manageUser"
        }
    ],

    menusVisiteur : [
        {
            name : "Se connecter",
            path : "/authentication"
        }
    ],

    menusClient : [
        {
            name : "Historique des commandes",
            path : "/historic"
        }
    ],

    menusCommuns : [
        {
            name : "Préférences",
            path : "/preferences"
        },
        {
            name : "Paramètres",
            path : "/parameters"
        }
    ]
};

export { methods };
// export de tous les menus pour le front et le web