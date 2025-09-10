# Consignes

✅ - Vous êtes développeur front-end : vous devez réaliser les consignes décrites dans le chapitre [Front-end](#Front-end)

✅ - Vous êtes développeur back-end : vous devez réaliser les consignes décrites dans le chapitre [Back-end](#Back-end) (*)

✅ - Vous êtes développeur full-stack : vous devez réaliser les consignes décrites dans le chapitre [Front-end](#Front-end) et le chapitre [Back-end](#Back-end) (*)

✅ (*) Afin de tester votre API, veuillez proposer une stratégie de test appropriée.

## Front-end

Le site de e-commerce d'Alten a besoin de s'enrichir de nouvelles fonctionnalités.

### Partie 1 : Shop

✅ - Afficher toutes les informations pertinentes d'un produit sur la liste
✅ - Permettre d'ajouter un produit au panier depuis la liste
✅ - Permettre de supprimer un produit du panier
✅ - Afficher un badge indiquant la quantité de produits dans le panier
✅ - Permettre de visualiser la liste des produits qui composent le panier.

### Partie 2

✅ - Créer un nouveau point de menu dans la barre latérale ("Contact")
✅ - Créer une page "Contact" affichant un formulaire
✅ - Le formulaire doit permettre de saisir son email, un message et de cliquer sur "Envoyer"
✅ - Email et message doivent être obligatoirement remplis, message doit être inférieur à 300 caractères.
✅ - Quand le message a été envoyé, afficher un message à l'utilisateur : "Demande de contact envoyée avec succès".

### Bonus : 

❌ - Ajouter un système de pagination et/ou de filtrage sur la liste des produits
❌ - On doit pouvoir visualiser et ajuster la quantité des produits depuis la liste et depuis le panier 

## Back-end

### Partie 1

Développer un back-end permettant la gestion de produits définis plus bas.
Vous pouvez utiliser la technologie de votre choix parmi la liste suivante :

❌- Node.js/Express
✅- Java/Spring Boot
❌- C#/.net Core
❌- PHP/Symphony : Utilisation d'API Platform interdite

Un produit a les caractéristiques suivantes : 

``` typescript
class Product {
  id: number;
  code: string;
  name: string;
  description: string;
  image: string;
  category: string;
  price: number;
  quantity: number;
  internalReference: string;
  shellId: number;
  inventoryStatus: "INSTOCK" | "LOWSTOCK" | "OUTOFSTOCK";
  rating: number;
  createdAt: number;
  updatedAt: number;
}
```

Le back-end créé doit pouvoir gérer les produits dans une base de données SQL/NoSQL ou dans un fichier json.

### Partie 2

✅ - Imposer à l'utilisateur de se connecter pour accéder à l'API.
  La connexion doit être gérée en utilisant un token JWT.  
  Deux routes devront être créées :
 * ✅ [POST] /account -> Permet de créer un nouveau compte pour un utilisateur avec les informations fournies par la requête.   
    Payload attendu : 
    ```
    {
      username: string,
      firstname: string,
      email: string,
      password: string
    }
    ```
 * ✅ [POST] /token -> Permet de se connecter à l'application.  
    Payload attendu :  
    ```
    {
      email: string,
      password: string
    }
    ```
    Une vérification devra être effectuée parmi tout les utilisateurs de l'application afin de connecter celui qui correspond aux infos fournies. Un token JWT sera renvoyé en retour de la reqûete.
✅ - Faire en sorte que seul l'utilisateur ayant le mail "admin@admin.com" puisse ajouter, modifier ou supprimer des produits. Une solution simple et générique devra être utilisée. Il n'est pas nécessaire de mettre en place une gestion des accès basée sur les rôles.
✅ - Ajouter la possibilité pour un utilisateur de gérer un panier d'achat pouvant contenir des produits.
✅ - Ajouter la possibilité pour un utilisateur de gérer une liste d'envie pouvant contenir des produits.

## Bonus

✅ Vous pouvez ajouter des tests Postman ou Swagger pour valider votre API

## Installation locale
- Frontend :
  - Prérequis : NPM
  - dans "/front" effectuer simplement "npm install" pour installer toutes les dépendances puis "npm start" pour lancer l'application
- Backend :
  - Prérequis : Java 17+
  - Version compilée du back : back/livrable/product-0.0.1-SNAPSHOT.jar
  - Lancer l'application en double cliquant sur cette archive. Vous pouvez vérifier que l'application est bien lancée en accédant à la console h2 (localhost:8080/h2-console)
    - Si l'application ne s'est pas lancée, verifiez que votre JAVA_HOME pointe bien vers un Java17+
    - Vous pouvez aussi lancer l'archive via une console pour plus d'informations sur l'erreur ('java -jar [archive]').
  - Accéder à la BDD H2 locale : 
    - JDBC URL : jdbc:h2:mem:productdb
    - User Name : sa    (pas de mot de passe)
  - Vous aurez une erreur : '"XXXX/productdb" not found, either pre-create it or allow remote database creation (not recommended in secure environments) [90149-232] 90149/90149 (Help)'
    - créer un fichier product.db au chemin indiqué
  - Scenarios de tests postman : back/postman/api-tests.json

## Informations complémentaires
  - Veuillez noter que la BDD H2 est actuellement configurée pour un usage local de demonstration et non pour un environnement de production :
    - Elle est réinitialisée au lancement de l'application et ne contient par défaut que les 30 produits communiqués initialement dans front/src/assets/products.json (réadapté au format SQL dans back/product/src/main/ressources en data.sql et schema.sql)
    - Les tests d'intégration partagent la même base de données (un rollback est effectué après chaque test)
