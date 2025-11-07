package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Modella lo schema di una tabella nel database relazionale. Ricava i metadati delle
 * colonne e mappa i tipi SQL a tipi Java.
 *
 * @author MAP corso
 * @version 1.0
 */
public class TableSchema {
    /**
     * Accesso al database
     */
    DbAccess db;

    /**
     * Inner class che rappresenta una colonna della tabella.
     */
    public class Column {
        private String name;
        private String type;

        /**
         * Costruttore della colonna.
         *
         * @param name nome della colonna
         * @param type tipo della colonna ("string" o "number")
         */
        Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        /**
         * Restituisce il nome della colonna.
         *
         * @return nome colonna
         */
        public String getColumnName() {
            return name;
        }

        /**
         * Verifica se la colonna contiene valori numerici.
         *
         * @return true se tipo "number", false altrimenti
         */
        public boolean isNumber() {
            return type.equals("number");
        }

        /**
         * Rappresentazione testuale della colonna.
         *
         * @return stringa "nome:tipo"
         */
        public String toString() {
            return name + ":" + type;
        }
    }

    /**
     * Lista delle colonne dello schema
     */
    List<Column> tableSchema = new ArrayList<Column>();

    /**
     * Costruttore che ricava lo schema di una tabella dal database.
     *
     * @param db accesso al database
     * @param tableName nome della tabella
     * @throws SQLException in caso di errore nell'accesso ai metadati
     */
    public TableSchema(DbAccess db, String tableName) throws SQLException {
        this.db = db;
        HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();

        // Mapping tipi SQL -> Java
        // http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
        mapSQL_JAVATypes.put("CHAR", "string");
        mapSQL_JAVATypes.put("VARCHAR", "string");
        mapSQL_JAVATypes.put("LONGVARCHAR", "string");
        mapSQL_JAVATypes.put("BIT", "string");
        mapSQL_JAVATypes.put("SHORT", "number");
        mapSQL_JAVATypes.put("INT", "number");
        mapSQL_JAVATypes.put("LONG", "number");
        mapSQL_JAVATypes.put("FLOAT", "number");
        mapSQL_JAVATypes.put("DOUBLE", "number");

        Connection con = db.getConnection();
        DatabaseMetaData meta = con.getMetaData();
        ResultSet res = meta.getColumns(null, null, tableName, null);

        while (res.next()) {
            if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
                tableSchema.add(
                        new Column(res.getString("COLUMN_NAME"), mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
        }
        res.close();
    }

    /**
     * Restituisce il numero di attributi (colonne) dello schema.
     *
     * @return numero di colonne
     */
    public int getNumberOfAttributes() {
        return tableSchema.size();
    }

    /**
     * Restituisce la colonna all'indice specificato.
     *
     * @param index indice della colonna
     * @return oggetto Column
     */
    public Column getColumn(int index) {
        return tableSchema.get(index);
    }
}
