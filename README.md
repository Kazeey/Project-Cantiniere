# ProjetCantiniere
Projet n°1 Aston.

# Installs

#### Angular
`npm install -g @angular/cli`.

#### Ionic
`npm install -g @ionic/cli`.

#### Express
`npm install express` in cmd, in the back folder.

#### Java
To open it on eclipse, `Files` > `Import` > `existing maven project`.

Update the project.

Run pom.xml as `Maven build`.

If you have specified a username/password for your SQL database, change the configuration in `src/main/java/ressources/application-mysql.properties`. 

# Api Java
This Api was given for the project.
To start the API, run `src/main/java/stone.lunchtime/spring/SpringBootConfiguration.java` as `Java Application`.

# Api Node
This project was generated with NodeJS version 14.8.0 and Express version 4.17.1.

#### Development server
Run `node app.js` for a dev server. Navigate to `http://localhost:3001/` 
(3001 is the default port, you can change it in `app.js`).

# Front Web
This project was generated with Angular version 10.1.2.

#### Development server
Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`.

#### Code scaffolding
Run `ng generate component component-name` to generate a new component.

#### Build
Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. 

Use the `--prod` flag for a production build.

#### Running unit tests
Run `ng test` to execute the unit tests via Karma.

# Front Mobile
This project was generated with Ionic version 5.0.1.

#### To run
As i saw, the commands are the same as angular for web.

#### Don't forget
When you add a new component, don't forget to create the files `nameComponent.module.ts` and `nameComponent-routing.module.ts`.

Then configure the files like the others, with the class export and other functions.

And then, if you need to add a route to this component, edit `app-routing.module.ts` to add a path.
