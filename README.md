# WomenShop - Système de Gestion de Stock

> Application desktop pour la gestion d'inventaire de boutique de mode féminine

## Description

WomenShop ("Boutique") est une application desktop professionnelle développée en JavaFX pour gérer l'inventaire d'une boutique de mode féminine. Elle offre une interface élégante avec toutes les fonctionnalités nécessaires pour la gestion de stock, les transactions, et le suivi financier.

## Fonctionnalités Principales

### Gestion des Produits
- Affichage de tous les produits avec détails complets
- Ajout, modification et suppression de produits
- Trois catégories : Vêtements, Chaussures, Accessoires
- Attributs spécifiques par catégorie (taille, couleur, matériau, marque, etc.)
- Gestion du stock avec indicateurs visuels

### Filtrage et Recherche
- Navigation par catégories via barre latérale élégante
- Recherche en temps réel par nom ou détails
- Tri multiple (prix, stock, nom)
- Combinaison de filtres et tri

### Transactions
- Achat d'articles (réapprovisionnement)
- Vente d'articles
- Validation du stock et du capital
- Confirmation avant chaque transaction
- Historique automatique en base de données

### Système de Remises
- Remises fixes par catégorie :
  - Vêtements : 30%
  - Chaussures : 20%
  - Accessoires : 50%
- Application/arrêt par catégorie
- Prix finaux calculés automatiquement

### 📊 Suivi Financier
- Tableau de bord en temps réel
- Capital disponible
- Revenu total
- Coût total
- Bénéfice net
- Formule : Capital = Initial + Revenu - Coût

### Validation des Données
- Validation complète des formulaires
- Messages d'erreur clairs
- Prévention des données invalides
- Vérification de cohérence (stock, capital)

## 🎨 Interface Utilisateur

### Design 
- Navigation intuitive par barre latérale
- Codage couleur (catégories, stock, remises)
- Animations de succès


## 🏗️ Architecture Technique

### Pattern MVC (Model-View-Controller)
```
📦 Model
 ├── Product (abstract)
 ├── Clothes
 ├── Shoes
 ├── Accessories
 ├── Transaction
 └── AppSettings

📦 View
 └── hello-view.fxml

📦 Controller
 ├── HelloController
 └── ProductDialogController
```

### Pattern DAO (Data Access Object)
```
📦 DAO Layer
 ├── DatabaseConnection (Singleton)
 ├── ProductDAO
 ├── TransactionDAO
 └── AppSettingsDAO
```

### Base de Données (3NF)
```
📦 MySQL Database: womenshop
 ├── products (table principale)
 ├── clothes (attributs spécifiques)
 ├── shoes (attributs spécifiques)
 ├── accessories (attributs spécifiques)
 ├── transactions (historique)
 └── app_settings (finances)
```

## Installation

### Prérequis
- **Java JDK 17** ou supérieur
- **MySQL 8.0** ou supérieur
- **Maven 3.6** ou supérieur
- **JavaFX 21** (inclus via Maven)

### Étape 1 : Cloner le Projet
```bash
git clone https://github.com/votre-repo/womenshop.git
cd womenshop
```

### Étape 2 : Configuration MySQL

#### Démarrer MySQL
```bash
# macOS
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows
# Via Services ou MySQL Workbench
```

#### Créer la Base de Données
```bash
mysql -u root -p
```

```sql
-- Copier-coller le contenu du fichier database_schema.sql
-- Ou importer directement :
source /chemin/vers/database_schema.sql
```

#### Vérifier l'Installation
```sql
USE womenshop;
SHOW TABLES;
-- Devrait afficher : products, clothes, shoes, accessories, transactions, app_settings

SELECT * FROM app_settings;
-- Devrait montrer : initial_capital = 10000.00
```

### Étape 3 : Configuration de l'Application

Modifier `src/main/java/com/example/projet_javafx/dao/DatabaseConnection.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/womenshop";
private static final String USER = "root";
private static final String PASSWORD = "VOTRE_MOT_DE_PASSE"; // ⚠️ IMPORTANT
```

### Étape 4 : Compilation et Exécution

#### Avec Maven
```bash
# Compiler le projet
mvn clean install

# Lancer l'application
mvn javafx:run
```

#### Avec IntelliJ IDEA
1. Ouvrir le projet dans IntelliJ
2. Marquer `src/main/java` comme Sources Root
3. Marquer `src/main/resources` comme Resources Root
4. Clic droit sur `HelloApplication.java` → Run

## 📁 Structure du Projet

```
womenshop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/projet_javafx/
│   │   │       ├── HelloApplication.java
│   │   │       ├── HelloController.java
│   │   │       ├── ProductDialogController.java
│   │   │       ├── model/
│   │   │       │   ├── Product.java
│   │   │       │   ├── Clothes.java
│   │   │       │   ├── Shoes.java
│   │   │       │   ├── Accessories.java
│   │   │       │   ├── ProductCategory.java
│   │   │       │   ├── Transaction.java
│   │   │       │   ├── TransactionType.java
│   │   │       │   └── AppSettings.java
│   │   │       └── dao/
│   │   │           ├── DatabaseConnection.java
│   │   │           ├── ProductDAO.java
│   │   │           ├── TransactionDAO.java
│   │   │           └── AppSettingsDAO.java
│   │   └── resources/
│   │       └── com/example/projet_javafx/
│   │           └── hello-view.fxml
├── database_schema.sql
├── pom.xml
└── README.md
```

## 💡 Guide d'Utilisation

### Démarrage Rapide

1. **Lancer l'application** → L'interface "Boutique" s'affiche
2. **Naviguer** → Utiliser les boutons de catégorie dans la barre latérale
3. **Chercher** → Taper dans la barre de recherche
4. **Trier** → Sélectionner un critère dans le menu déroulant

### Ajouter un Produit
1. Cliquer sur **"New Product"**
2. Remplir le formulaire :
   - Nom, Prix d'achat, Prix de vente
   - Sélectionner la catégorie
   - Remplir les champs spécifiques (taille, couleur, etc.)
3. Cliquer **"Save Product"**

### Acheter des Articles
1. Trouver le produit dans le tableau
2. Cliquer **"Buy"**
3. Entrer la quantité
4. Confirmer l'achat
5. Stock augmente, Capital diminue

### Vendre des Articles
1. Trouver le produit dans le tableau
2. Cliquer **"Sell"**
3. Entrer la quantité
4. Confirmer la vente
5. Stock diminue, Capital augmente

### Appliquer une Remise
1. Cliquer **"Apply Discount"**
2. Choisir la catégorie
3. Confirmer
4. Tous les produits de la catégorie sont remisés

## Données de Test

La base de données contient **22 produits d'exemple** :

### Vêtements (8)
- Summer Floral Dress
- Leather Jacket
- Casual Denim Jeans
- Silk Evening Gown
- Cotton T-Shirt
- Wool Cardigan
- Elegant Blazer
- Maxi Skirt

### Chaussures (6)
- Running Sneakers
- High Heels
- Ballet Flats
- Ankle Boots
- Sandals
- Wedge Heels

### Accessoires (8)
- Designer Handbag
- Silver Necklace
- Silk Scarf
- Leather Belt
- Sunglasses
- Fashion Watch
- Pearl Earrings
- Crossbody Bag

**Capital Initial** : €10,000.00


## 👥 Équipe

- **Uliana Chernysheva** - Développement
- **Joss Develter** - Développement
