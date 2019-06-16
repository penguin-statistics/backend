db = connect("localhost:27017/admin");

db.auth("root", "root");

db = db.getSiblingDB("penguin_stats");

db.createUser(
    {
      user: "root",
      pwd: "root",
      roles: [ { role: "dbOwner", db: "penguin_stats" }]
    }
  );
