package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import database.TableSchema.Column;

/**
 * Classe per l'accesso ai dati di una tabella del database.
 * Fornisce metodi per estrarre transazioni distinte, valori distinti di colonne
 * e valori aggregati (MIN, MAX).
 *
 * @author A. Appice, Lombardi Costantino
 * @version 1.0.0
 */
public class TableData {

    /**
     * Accesso al database.
     */
    DbAccess db;

    /**
     * Costruttore che inizializza l'accesso al database.
     *
     * @param db oggetto DbAccess per connessione al database
     */
    public TableData(DbAccess db) {
        this.db = db;
    }

    /**
     * Ricava le transazioni (righe) distinte dalla tabella specificata.
     *
     * @param table nome della tabella nel database
     * @return lista di Example rappresentanti le transazioni distinte
     * @throws SQLException in caso di errore SQL
     * @throws EmptySetException se la query restituisce un ResultSet vuoto
     */
    public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException {
        LinkedList<Example> transSet = new LinkedList<Example>();
        Statement statement;
        String quotedTable = DbAccess.quoteIdentifier(table, "Nome tabella");
        TableSchema tSchema = new TableSchema(db, table);

        String query = "select distinct ";

        for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
            Column c = tSchema.getColumn(i);
            if (i > 0)
                query += ",";
            query += DbAccess.quoteIdentifier(c.getColumnName(), "Nome colonna");
        }
        if (tSchema.getNumberOfAttributes() == 0)
            throw new SQLException();
        query += (" FROM " + quotedTable);

        statement = db.getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);
        boolean empty = true;
        while (rs.next()) {
            empty = false;
            Example currentTuple = new Example();
            for (int i = 0; i < tSchema.getNumberOfAttributes(); i++)
                if (tSchema.getColumn(i).isNumber())
                    currentTuple.add(rs.getDouble(i + 1));
                else
                    currentTuple.add(rs.getString(i + 1));
            transSet.add(currentTuple);
        }
        rs.close();
        statement.close();
        if (empty)
            throw new EmptySetException();

        return transSet;
    }

    /**
     * Ricava i valori distinti di una colonna, ordinati in modo crescente.
     *
     * @param table nome della tabella
     * @param column oggetto Column rappresentante la colonna
     * @return insieme di valori distinti ordinati (TreeSet)
     * @throws SQLException in caso di errore SQL
     */
    public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
        Set<Object> valueSet = new TreeSet<Object>();
        Statement statement;
        String quotedTable = DbAccess.quoteIdentifier(table, "Nome tabella");
        String quotedColumn = DbAccess.quoteIdentifier(column.getColumnName(), "Nome colonna");

        String query = "select distinct ";
        query += quotedColumn;
        query += (" FROM " + quotedTable);
        query += (" ORDER BY " + quotedColumn);

        statement = db.getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            if (column.isNumber())
                valueSet.add(rs.getDouble(1));
            else
                valueSet.add(rs.getString(1));
        }
        rs.close();
        statement.close();

        return valueSet;
    }

    /**
     * Calcola un valore aggregato (MIN o MAX) per una colonna.
     *
     * @param table nome della tabella
     * @param column oggetto Column rappresentante la colonna
     * @param aggregate tipo di aggregazione (MIN o MAX)
     * @return valore aggregato (Double per numerici, String per stringhe)
     * @throws SQLException in caso di errore SQL
     * @throws NoValueException se il valore aggregato è null
     */
    public Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate)
            throws SQLException, NoValueException {
        Statement statement;
        Object value = null;
        String aggregateOp = "";
        String quotedTable = DbAccess.quoteIdentifier(table, "Nome tabella");
        String quotedColumn = DbAccess.quoteIdentifier(column.getColumnName(), "Nome colonna");

        String query = "select ";
        if (aggregate == QUERY_TYPE.MAX)
            aggregateOp += "max";
        else
            aggregateOp += "min";
        query += aggregateOp + "(" + quotedColumn + ") FROM " + quotedTable;

        statement = db.getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);
        if (rs.next()) {
            if (column.isNumber())
                value = rs.getFloat(1);
            else
                value = rs.getString(1);
        }
        rs.close();
        statement.close();
        if (value == null)
            throw new NoValueException("No " + aggregateOp + " on " + column.getColumnName());

        return value;
    }
}
