package ru.bright.managers;

import ru.bright.User;
import ru.bright.model.Flat;
import ru.bright.server.Server;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * Класс для управления коллекцией
 * Обеспечивает добавление, удаление, поиск, сохранение и загрузку коллекции из файла.
 */
public class CollectionManager {

    /**
     * Время создания коллекции
     */
    private static java.time.ZonedDateTime creationDate;

    /**
     * Коллекция объектов
     */
    private static HashSet<Flat> collection;

    private CollectionDatabaseManager collectionDbManager;

    private Server server;


    public CollectionManager(Server server, CollectionDatabaseManager collectionDbManager) {
        collection = new HashSet<>();
        this.collectionDbManager = collectionDbManager;
        this.creationDate = java.time.ZonedDateTime.now();
        this.server = server;
    }

    /**
     * Добавляет объект 
     * @param flat добавляемый объект
     */
    public synchronized void add(Flat flat) throws SQLException {
        assert flat != null;
        collectionDbManager.addFlatJson(flat);
        collection.add(flat);
    }

    /**
     * Загружает коллекцию из файла
     * @return успешно ли загрузилась коллекция
     */
    public synchronized boolean loadJsonCollectionFromDatabase() {
        try {
            collectionDbManager.initCollectionTable();
        } catch (SQLException ex) {
            server.shutdown();
        }

        Set<Flat> set = collectionDbManager.loadAllFlatsDB();
        if(set == null) return false;
        collection = (HashSet<Flat>) set;
        return true;
    }

    /**
     * Удаляет из коллекции элементы меньшие заданного
     * @param flat объект для сравнения
     * @return
     */
    public synchronized void removeLower(User user, Flat flat) throws SQLException {
        //   collection.removeIf(el -> (el.compareTo(flat) < 0));
        List<Flat> s =  collection.stream().filter(el -> (el.compareTo(flat) < 0)).collect(Collectors.toList());
        for (Flat f : s) {
            if(f.getOwnerLogin().equals(user.getLogin())) {
                collectionDbManager.deleteFlatById(f.getId());
                collection.remove(f);
            }
        }
    }

    public synchronized void update(Flat flat) throws SQLException {
        collectionDbManager.update(flat);
    }

    /**
     * Фильтрует элементы, имена которых начинается с подстроки
     * @param name подстрока
     * @return отфильтрованный список
     */
    public synchronized List<Flat> filterStartsWithName(String name) {
        return collection.stream().filter(flat -> flat.getName().startsWith(name))
                .toList();
    }

    /**
     * Возвращает максимальный элемент коллекции
     * @return максимальный элемент
     */
    public synchronized Flat getMaxElement() {
        return collection.stream().max(Comparator.naturalOrder()).get();
    }

    /**
     * Ищет объект по заданому ID
     * @param id заданный идентификатор
     * @return элемент с заданным ID
     */
    public synchronized Flat getById(Long id) {
        return collection.stream().filter(flat -> flat.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет из коллекции элемент с заданным ID
     * @param id заданный идентификатор
     */
    public synchronized void deleteById(Long id) throws SQLException {
        collectionDbManager.deleteFlatById(id);
        collection.removeIf(flat -> (flat.getId() == id));
    }

    /**
     * Очищает коллекцию
     */
    public void clear(User user) {
        collection.stream().forEach(flat -> {
            try {
                if(user.getLogin() == flat.getOwnerLogin()) {
                    deleteById(flat.getId());
                }
            } catch (SQLException e) {
                server.getLogger().log(Level.SEVERE,"SQL error", e);
            }
        });
    }

    /**
     * Возвращает дату создания коллекции
     * @return дата
     */
    public static ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Возвращает неизменяемую коллекцию
     * @return неизменяемая коллекция
     */
    public Set<Flat> getUnmodifiableCollection() {
        return Collections.unmodifiableSet(collection);
    }







}
