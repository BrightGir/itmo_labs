package ru.bright.managers;

import ru.bright.model.Flat;

import java.time.ZonedDateTime;
import java.util.*;


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

    /**
     * Менеджер файлов для загрузки и сохранения коллекции
     */
    private FileManager fileManager;

    /**
     * Конструктор (вызывается только в {@link ru.bright.Main})
     * @param fileManager менеджер файла
     */
    public CollectionManager(FileManager fileManager) {
        collection = new HashSet<>();
        this.creationDate = java.time.ZonedDateTime.now();
        this.fileManager = fileManager;
    }

    public CollectionManager() {
        if(collection == null) {
            collection = new HashSet<>();
            this.creationDate = java.time.ZonedDateTime.now();
        }
    }

    /**
     * Добавляет объект 
     * @param flat добавляемый объект
     */
    public void add(Flat flat) {
        assert flat != null;
        collection.add(flat);
    }

    /**
     * Сохраняет коллекцию в файл
     * @return успешно ли сохранилась коллекция
     */
    public boolean saveJsonCollectionToFile() {
        return fileManager.writeJsonCollection(collection);
    }

    /**
     * Загружает коллекцию из файла
     * @return успешно ли загрузилась коллекция
     */
    public boolean loadJsonCollectionFromFile() {
        HashSet<Flat> set = fileManager.readJsonCollection();
        if(set == null) return false;
        collection = set;
        return true;
    }

    /**
     * Удаляет из коллекции элементы меньшие заданного
     * @param flat объект для сравнения
     * @return
     */
    public void removeLower(Flat flat) {
        collection.removeIf(el -> (el.compareTo(flat) < 0));
    }

    /**
     * Фильтрует элементы, имена которых начинается с подстроки
     * @param name подстрока
     * @return отфильтрованный список
     */
    public List<Flat> filterStartsWithName(String name) {
        return collection.stream().filter(flat -> flat.getName().startsWith(name))
                .toList();
    }

    /**
     * Возвращает максимальный элемент коллекции
     * @return максимальный элемент
     */
    public Flat getMaxElement() {
        return collection.stream().max(Comparator.naturalOrder()).get();
    }

    /**
     * Ищет объект по заданому ID
     * @param id заданный идентификатор
     * @return элемент с заданным ID
     */
    public Flat getById(Long id) {
        return collection.stream().filter(flat -> flat.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет из коллекции элемент с заданным ID
     * @param id заданный идентификатор
     */
    public void deleteById(Long id) {
        collection.removeIf(flat -> flat.getId() == id);
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        collection.clear();
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
