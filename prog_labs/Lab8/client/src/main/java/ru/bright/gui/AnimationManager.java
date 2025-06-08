package ru.bright.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.bright.model.Flat;

import java.util.*;

public class AnimationManager {


    private List<AnimatingBatch> activeAnimatingBatches = new ArrayList<>();
    private AnimationTimer masterAnimationTimer; // Один таймер на все
    private static final long APPEAR_ANIMATION_DURATION_MS = 3000;
    private MainWindow window;
    private final float MIN_VISIBLE = 8;
    private Canvas createdCanvas;

    public AnimationManager(MainWindow window) {
        this.window = window;
    }

    /*
    private void drawFlatsOnCanvas(GraphicsContext gc, double currentDrawingScale) {
        Set<Flat> allFlats = window.getActualFlats(); // Используем this.actualFlats

        if (allFlats == null || allFlats.isEmpty()) {
            return;
        }

        // Начальные МИРОВЫЕ координаты для размещения первой квартиры
        double originWorldX = 10;
        double originWorldY = -10; // Y "вверх" в мировых координатах, если начало в центре
        double worldSpacing = 15 / currentDrawingScale; // Отступ, адаптированный к масштабу

        double currentWorldY = originWorldY; // Это будет "линия", НАД которой рисуется верх квартиры
        double currentWorldX = originWorldX;

        long currentTimeNanosForDraw = (masterAnimationTimer != null && !activeAnimatingBatches.isEmpty()) ?
                System.nanoTime() : 0;

        for (Flat flat : allFlats) {
            if (flat == null) continue;
            double area = flat.getArea();
            if (area <= 0) continue;

            double baseSizeInWorld = Math.sqrt(area) * 1.5;
            double minVisibleScreenSize = 15.0; // Минимальный размер на экране
            double targetSizeInWorld = Math.max(minVisibleScreenSize / currentDrawingScale, baseSizeInWorld);

            double currentVisibleSize = targetSizeInWorld;
            double currentOpacity = 1.0;
            double animationProgressForThisFlat = 1.0;

            if (currentTimeNanosForDraw > 0) { // Только если есть активный таймер
                for (AnimatingBatch batch : activeAnimatingBatches) {
                    if (batch.flatIds.contains(flat.getId())) {
                        animationProgressForThisFlat = batch.getCurrentProgress(currentTimeNanosForDraw, APPEAR_ANIMATION_DURATION_MS);
                        break;
                    }
                }
            }

            if (animationProgressForThisFlat < 1.0) {
                currentOpacity = animationProgressForThisFlat;
                currentVisibleSize = targetSizeInWorld * (0.2 + 0.8 * animationProgressForThisFlat);
                currentVisibleSize = Math.max(5.0 / currentDrawingScale, currentVisibleSize);
            }

            // xWorld, yWorld - координаты ВЕРХНЕГО ЛЕВОГО УГЛА квартиры в МИРОВОМ пространстве
            double xWorld = currentWorldX;
            double yWorld = currentWorldY - currentVisibleSize;

            String ownerLogin = flat.getOwnerLogin();
            Color baseColor = generateColorFromString(ownerLogin);


            gc.setGlobalAlpha(currentOpacity);

            gc.setFill(Color.rgb(0, 0, 0, 0.15 * currentOpacity));
            double shadowOffsetOnScreen = 3.0;
            double shadowOffsetInWorld = shadowOffsetOnScreen / currentDrawingScale;
            gc.fillRect(xWorld + shadowOffsetInWorld, yWorld + shadowOffsetInWorld, currentVisibleSize, currentVisibleSize);

            Stop[] stops = new Stop[]{
                    new Stop(0, baseColor.deriveColor(0, 1.0, 1.15, 1.0)),
                    new Stop(1, baseColor)
            };
            LinearGradient lg = new LinearGradient(xWorld, yWorld, xWorld, yWorld + currentVisibleSize, false, CycleMethod.NO_CYCLE, stops);
            gc.setFill(lg);
            gc.fillRect(xWorld, yWorld, currentVisibleSize, currentVisibleSize);

            gc.setStroke(baseColor.darker().darker());
            gc.setLineWidth(1.0 / currentDrawingScale); // Адаптивная толщина обводки
            gc.strokeRect(xWorld, yWorld, currentVisibleSize, currentVisibleSize);

            gc.setGlobalAlpha(1.0);

            currentWorldY = yWorld - worldSpacing; // Следующая квартира будет ниже
        }
    }

     */

    /*
    private void drawFlatsOnCanvas(GraphicsContext gc, double currentDrawingScale) {
        Set<Flat> allFlats = window.getActualFlats();

        if (allFlats == null || allFlats.isEmpty()) {
            return;
        }

        double originWorldX = 10;
        double originWorldY = -10;
        double worldSpacing = 15 / currentDrawingScale;

        double currentWorldY = originWorldY;
        double currentWorldX = originWorldX;

        long currentTimeNanosForDraw = (masterAnimationTimer != null && !activeAnimatingBatches.isEmpty()) ?
                System.nanoTime() : 0;

        for (Flat flat : allFlats) {
            if (flat == null) continue;
            double area = flat.getArea();
            if (area <= 0) continue;

            double baseSizeInWorld = Math.sqrt(area) * 1.5;
            double minVisibleScreenSize = MIN_VISIBLE; // Минимальный РАЗМЕР НА ЭКРАНЕ в пикселях

            // targetSizeInWorld - это размер в МИРОВЫХ координатах.
            // Эта логика гарантирует, что объект на экране будет не меньше minVisibleScreenSize.
            // Если вы хотите, чтобы объекты могли становиться сколь угодно маленькими на экране,
            // измените эту строку на: double targetSizeInWorld = baseSizeInWorld;
            double targetSizeInWorld = Math.max(minVisibleScreenSize / currentDrawingScale, baseSizeInWorld);

            double currentVisibleSize = targetSizeInWorld;
            double currentOpacity = 1.0;
            double animationProgressForThisFlat = 1.0;

            if (currentTimeNanosForDraw > 0) {
                for (AnimatingBatch batch : activeAnimatingBatches) {
                    if (batch.flatIds.contains(flat.getId())) {
                        animationProgressForThisFlat = batch.getCurrentProgress(currentTimeNanosForDraw, APPEAR_ANIMATION_DURATION_MS);
                        break;
                    }
                }
            }

            if (animationProgressForThisFlat < 1.0) {
                currentOpacity = animationProgressForThisFlat;
                currentVisibleSize = targetSizeInWorld * (0.2 + 0.8 * animationProgressForThisFlat);
                // Это дополнительное ограничение на минимальный размер во время анимации (5 пикселей на экране).
                // Если оно не нужно, или нужно другое, измените или удалите.
                currentVisibleSize = Math.max(5.0 / currentDrawingScale, currentVisibleSize);
            }

            double xWorld = flat.getCoordinates().getX();
            double yWorld = flat.getCoordinates().getY();
          //  double xWorld = currentWorldX;
          //  double yWorld = currentWorldY - currentVisibleSize;

            String ownerLogin = flat.getOwnerLogin();
            Color baseColor = generateColorFromString(ownerLogin);

            gc.setGlobalAlpha(currentOpacity);

            // Тень (опционально)
            gc.setFill(Color.rgb(0, 0, 0, 0.15 * currentOpacity));
            double shadowOffsetOnScreen = 3.0;
            double shadowOffsetInWorld = shadowOffsetOnScreen / currentDrawingScale;
            gc.fillRect(xWorld + shadowOffsetInWorld, yWorld + shadowOffsetInWorld, currentVisibleSize, currentVisibleSize);

            Stop[] stops = new Stop[]{
                    new Stop(0, baseColor.deriveColor(0, 1.0, 1.15, 1.0)),
                    new Stop(1, baseColor)
            };
            LinearGradient lg = new LinearGradient(xWorld, yWorld, xWorld, yWorld + currentVisibleSize, false, CycleMethod.NO_CYCLE, stops);
            gc.setFill(lg);
            gc.fillRect(xWorld, yWorld, currentVisibleSize, currentVisibleSize);

            gc.setStroke(baseColor.darker().darker());
            gc.setLineWidth(1.0 / currentDrawingScale);
            gc.strokeRect(xWorld, yWorld, currentVisibleSize, currentVisibleSize);

            gc.setGlobalAlpha(1.0);

            currentWorldY = yWorld - worldSpacing;
        }
    }
*/
    private void drawFlatsOnCanvas(GraphicsContext gc, double currentDrawingScale) {
        Set<Flat> allFlats = window.getActualFlats();

        if (allFlats == null || allFlats.isEmpty()) {
            return;
        }

        long currentTimeNanosForDraw = (masterAnimationTimer != null && !activeAnimatingBatches.isEmpty()) ?
                System.nanoTime() : 0;

        for (Flat flat : allFlats) {
            if (flat == null) continue;
            double area = flat.getArea();
            if (area <= 0) continue;

            // === Расчет РАЗМЕРА объекта (остается как есть) ===
            double baseSizeInWorld = Math.sqrt(area) * 1.5;
            double minVisibleScreenSize = MIN_VISIBLE;
            double targetSizeInWorld = Math.max(minVisibleScreenSize / currentDrawingScale, baseSizeInWorld);
            double currentVisibleSize = targetSizeInWorld; // Это высота и ширина квадрата в мировых единицах
            double currentOpacity = 1.0;
            double animationProgressForThisFlat = 1.0;

            if (currentTimeNanosForDraw > 0) {
                for (AnimatingBatch batch : activeAnimatingBatches) {
                    if (batch.flatIds.contains(flat.getId())) {
                        animationProgressForThisFlat = batch.getCurrentProgress(currentTimeNanosForDraw, APPEAR_ANIMATION_DURATION_MS);
                        break;
                    }
                }
            }

            if (animationProgressForThisFlat < 1.0) {
                currentOpacity = animationProgressForThisFlat;
                currentVisibleSize = targetSizeInWorld * (0.2 + 0.8 * animationProgressForThisFlat);
                currentVisibleSize = Math.max(5.0 / currentDrawingScale, currentVisibleSize);
            }
            // === Конец расчета РАЗМЕРА ===

            // === Определение ПОЗИЦИИ объекта ===
            // Предполагаем:
            // flat.getCoordinates().getX() -> мировая X-координата ЛЕВОГО КРАЯ квадрата.
            // flat.getCoordinates().getY() -> мировая Y-координата ВЕРХНЕГО КРАЯ квадрата (в вашей системе, где Y растет вверх).
            double x_left_world = flat.getCoordinates().getX();
            double y_top_world = flat.getCoordinates().getY(); // Это верхняя линия объекта в мировой системе Y-up

            // Для gc.fillRect(x, y, width, height), 'y' это верхний левый угол, и он рисует ВНИЗ.
            // Наша мировая Y растет ВВЕРХ.
            // Значит, y для fillRect должен быть y_top_world - currentVisibleSize
            double xRect = x_left_world;
            double yRect = y_top_world - currentVisibleSize; // y-координата верхнего левого угла для fillRect

            String ownerLogin = flat.getOwnerLogin();
            Color baseColor = generateColorFromString(ownerLogin);

            gc.setGlobalAlpha(currentOpacity);

            // Тень
            gc.setFill(Color.rgb(0, 0, 0, 0.15 * currentOpacity));
            double shadowOffsetOnScreen = 3.0;
            double shadowOffsetInWorld = shadowOffsetOnScreen / currentDrawingScale;
            // Тень рисуется относительно xRect, yRect
            gc.fillRect(xRect + shadowOffsetInWorld, yRect + shadowOffsetInWorld, currentVisibleSize, currentVisibleSize);

            // Градиент
            Stop[] stops = new Stop[]{
                    new Stop(0, baseColor.deriveColor(0, 1.0, 1.15, 1.0)),
                    new Stop(1, baseColor)
            };
            // Координаты для градиента также должны соответствовать прямоугольнику, который будет нарисован
            LinearGradient lg = new LinearGradient(xRect, yRect, xRect, yRect + currentVisibleSize, false, CycleMethod.NO_CYCLE, stops);
            gc.setFill(lg);
            gc.fillRect(xRect, yRect, currentVisibleSize, currentVisibleSize);

            // Обводка
            gc.setStroke(baseColor.darker().darker());
            gc.setLineWidth(1.0 / currentDrawingScale);
            gc.strokeRect(xRect, yRect, currentVisibleSize, currentVisibleSize);

            gc.setGlobalAlpha(1.0);

            // Строка currentWorldY = yWorld - worldSpacing; больше не нужна в таком виде,
            // так как объекты позиционируются индивидуально.
        }
    }

    public Canvas createInteractiveCanvas() {
        createdCanvas = new Canvas(window.CANVAS_WIDTH, window.CANVAS_HEIGHT);
        final GraphicsContext gc = createdCanvas.getGraphicsContext2D();
        //   Font ubuntuFont = Font.loadFont(
        //           AuthWindow.class.getResourceAsStream("/fonts/Ubuntu-Regular.ttf"), 10);
        final double[] scale = {1.0};
        // Начальное смещение, чтобы (0,0) мировых координат было в центре холста
        final double[] offsetX = {window.CANVAS_WIDTH / 2.0};
        final double[] offsetY = {window.CANVAS_HEIGHT / 2.0};
        final double MIN_SCALE = 0.1;
        final double MAX_SCALE = 10.0;

        final double[] lastMouseXForPan = {-1};
        final double[] lastMouseYForPan = {-1};

        final Flat[] currentlyHoveredFlatArray = {null};
        final double[] currentScreenMouseX = {-1};
        final double[] currentScreenMouseY = {-1};
        final double[] worldMouseX = {-1};
        final double[] worldMouseY = {-1};

        final double WORLD_MARGIN = 40;
        final double WORLD_GRID_UNIT = 50;

        final boolean[] needsFullRedraw = {true};
        final boolean[] needsHoverInfoRedraw = {true};

        // Размеры информационного блока будут вычисляться динамически
        // final double[] infoBoxWidth = {0}; // Больше не нужны как поля, вычисляются в drawHoverInfo
        // final double[] infoBoxHeight = {0};
        final double INFO_BOX_PADDING = 8;
        final double INFO_BOX_OFFSET_X = 15;
        final double INFO_BOX_OFFSET_Y = 10;

        Runnable drawBackground = () -> {
            gc.save();
            gc.setTransform(1, 0, 0, 1, 0, 0);
            gc.clearRect(0, 0, window.CANVAS_WIDTH, window.CANVAS_HEIGHT);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            gc.strokeRect(0, 0, window.CANVAS_WIDTH, window.CANVAS_HEIGHT);

            gc.translate(offsetX[0], offsetY[0]);
            gc.scale(scale[0], scale[0]);

            double viewPortWorldWidth = window.CANVAS_WIDTH / scale[0];
            double viewPortWorldHeight = window.CANVAS_HEIGHT / scale[0];
            double worldViewOriginX = -offsetX[0] / scale[0];
            double worldViewOriginY = -offsetY[0] / scale[0];

            double gridDrawMinX = worldViewOriginX - viewPortWorldWidth; // Немного расширим, чтобы сетка гарантированно покрывала
            double gridDrawMaxX = worldViewOriginX + viewPortWorldWidth;
            double gridDrawMinY = worldViewOriginY - viewPortWorldHeight;
            double gridDrawMaxY = worldViewOriginY + viewPortWorldHeight;


            gc.setStroke(Color.LIGHTGRAY);
            gc.setLineWidth(0.5 / scale[0]);

            gc.setFont(Font.font("Ubuntu-Regular",9/scale[0]));

            for (double yLine = Math.floor(gridDrawMinY / WORLD_GRID_UNIT) * WORLD_GRID_UNIT; yLine <= gridDrawMaxY; yLine += WORLD_GRID_UNIT) {
                gc.strokeLine(gridDrawMinX - WORLD_GRID_UNIT, yLine, gridDrawMaxX + WORLD_GRID_UNIT, yLine); // Рисуем линии чуть шире видимой области
                // Рисуем метку, если она попадает в видимую область + небольшой запас
                if (yLine >= worldViewOriginY - viewPortWorldHeight && yLine <= worldViewOriginY + viewPortWorldHeight) {
                    gc.setFill(Color.DIMGRAY);
                    gc.fillText(String.valueOf((int) yLine), 0 + 5 / scale[0], yLine + 4 / scale[0]);
                }
            }
            for (double xLine = Math.floor(gridDrawMinX / WORLD_GRID_UNIT) * WORLD_GRID_UNIT; xLine <= gridDrawMaxX; xLine += WORLD_GRID_UNIT) {
                gc.strokeLine(xLine, gridDrawMinY - WORLD_GRID_UNIT, xLine, gridDrawMaxY + WORLD_GRID_UNIT); // Рисуем линии чуть выше/ниже видимой области
                // Рисуем метку, если она попадает в видимую область + небольшой запас
                if (xLine >= worldViewOriginX - viewPortWorldWidth && xLine <= worldViewOriginX + viewPortWorldWidth) {
                    gc.setFill(Color.DIMGRAY);
                    gc.fillText(String.valueOf((int) xLine), xLine - 8 / scale[0], 0 + 15 / scale[0]);
                }
            }
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0 / scale[0]);
            gc.strokeLine(gridDrawMinX - WORLD_GRID_UNIT, 0, gridDrawMaxX + WORLD_GRID_UNIT, 0);
            gc.strokeLine(0, gridDrawMinY - WORLD_GRID_UNIT, 0, gridDrawMaxY + WORLD_GRID_UNIT);
            gc.restore();
        };


        Runnable drawFlatsLayer = () -> {
            gc.save(); // Сохраняем "чистое" состояние gc (экранные координаты)

            // Применяем те же самые глобальные трансформации, что и для сетки
            gc.translate(offsetX[0], offsetY[0]);
            gc.scale(scale[0], scale[0]);

            // Вызываем ВАШ ЕДИНЫЙ метод отрисовки квартир
            drawFlatsOnCanvas(gc, scale[0]); // Передаем gc и текущий масштаб

            gc.restore(); // Восстанавливаем gc до экранных координат
        };

        Runnable drawHoverInfo = () -> {
            if (currentlyHoveredFlatArray[0] == null || currentScreenMouseX[0] == -1) {
                return;
            }

            gc.save();
            gc.setTransform(1, 0, 0, 1, 0, 0);

          //  Font infoFont = Font.loadFont(MainWindow.class.getResourceAsStream("/fonts/Ubuntu-Regular.ttf"), 20);
            Font infoFont = Font.font("Ubuntu-Regular", 20);
            gc.setFont(infoFont);

            List<String> lines = new ArrayList<>();
            //lines.add("Area: " + currentlyHoveredFlatArray[0].getArea());
            lines.add(window.getBundle().getString("info.id") + ": " + currentlyHoveredFlatArray[0].getId());
            lines.add(window.getBundle().getString("info.owner") + ": " + currentlyHoveredFlatArray[0].getOwnerLogin());
            lines.add(window.getBundle().getString("info.name") + ": " + currentlyHoveredFlatArray[0].getName());
            lines.add(window.getBundle().getString("info.area") + ": " + currentlyHoveredFlatArray[0].getArea());
            lines.add(window.getBundle().getString("info.rooms") + ": " + currentlyHoveredFlatArray[0].getNumberOfRooms());




        //    String name = currentlyHoveredFlatArray[0].getName();
        //    if (name.length() > 30) name = name.substring(0, 27) + "..."; // Увеличил немного лимит для имени
        //    lines.add("Name: " + name);

            double currentInfoBoxWidth = 0;
            Text tempText = new Text(); // Используем один объект Text для измерений
            tempText.setFont(infoFont);

            for (String line : lines) {
                tempText.setText(line);
                currentInfoBoxWidth = Math.max(currentInfoBoxWidth, tempText.getLayoutBounds().getWidth());
            }
            currentInfoBoxWidth += 2 * INFO_BOX_PADDING;
            // Высота строки более точно через getLineSpacing или getBaselineOffset + getHeight, но для простоты так:
            double singleLineHeight = tempText.getLayoutBounds().getHeight() * 0.85; // Приблизительная высота строки с небольшим запасом
            double currentInfoBoxHeight = lines.size() * singleLineHeight + 2 * INFO_BOX_PADDING;


            double boxX = currentScreenMouseX[0] + INFO_BOX_OFFSET_X;
            double boxY = currentScreenMouseY[0] + INFO_BOX_OFFSET_Y;

            if (boxX + currentInfoBoxWidth > window.CANVAS_WIDTH - 1) {
                boxX = currentScreenMouseX[0] - currentInfoBoxWidth - INFO_BOX_OFFSET_X;
            }
            if (boxY + currentInfoBoxHeight > window.CANVAS_HEIGHT - 1) {
                boxY = currentScreenMouseY[0] - currentInfoBoxHeight - INFO_BOX_OFFSET_Y;
            }
            boxX = Math.max(1, boxX);
            boxY = Math.max(1, boxY);

            gc.setFill(Color.WHITE);
            gc.fillRect(boxX, boxY, currentInfoBoxWidth, currentInfoBoxHeight);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            gc.strokeRect(boxX, boxY, currentInfoBoxWidth, currentInfoBoxHeight);

            gc.setFill(Color.BLACK);
            double textY = boxY + INFO_BOX_PADDING + singleLineHeight * 0.85; // Скорректировано для лучшего вертикального центрирования первой строки
            for (String line : lines) {
                gc.fillText(line, boxX + INFO_BOX_PADDING, textY);
                textY += singleLineHeight;
            }
            gc.restore();
        };

        /*
        Runnable mainRenderLoop = () -> {
            if (needsFullRedraw[0]) {
                drawBackground.run();
                drawFlatsLayer.run();
                needsFullRedraw[0] = false;
                needsHoverInfoRedraw[0] = true;
            } else if (needsHoverInfoRedraw[0]) {
                drawBackground.run();
                drawFlatsLayer.run();
            }

            if (needsHoverInfoRedraw[0] || currentlyHoveredFlatArray[0] != null) {
                drawHoverInfo.run(); // drawHoverInfo сама проверит, есть ли currentlyHoveredFlat
                needsHoverInfoRedraw[0] = false;
            }
        };
        */
        Runnable mainRenderLoop = () -> {
            // Если требуется полная перерисовка (масштаб, панорамирование, изменение набора квартир)
            // или если нужно обновить информацию о наведении (которая рисуется поверх всего)
            if (needsFullRedraw[0] || needsHoverInfoRedraw[0]) {
                drawBackground.run();     // Всегда начинаем с чистого фона и сетки
                drawFlatsLayer.run();     // Затем рисуем основные объекты (квартиры)
                drawHoverInfo.run();      // Затем рисуем информацию о наведении (если есть что рисовать)

                needsFullRedraw[0] = false;
                needsHoverInfoRedraw[0] = false; // Сбрасываем оба флага, так как все перерисовали
            }
            // Если никаких флагов не было, значит, ничего не изменилось, и перерисовка не нужна.
        };

        createdCanvas.getProperties().put("mainRenderLoop", mainRenderLoop);
        createdCanvas.getProperties().put("needsFullRedrawFlag", needsFullRedraw);

        createdCanvas.setOnMousePressed((MouseEvent event) -> {
            if (event.isMiddleButtonDown() || (event.isPrimaryButtonDown() && event.isShiftDown())) {
                lastMouseXForPan[0] = event.getX();
                lastMouseYForPan[0] = event.getY();
                event.consume();
            }
        });

        createdCanvas.setOnMouseDragged((MouseEvent event) -> {
            if ((event.isMiddleButtonDown() || (event.isPrimaryButtonDown() && event.isShiftDown())) && lastMouseXForPan[0] != -1) {
                double dx = event.getX() - lastMouseXForPan[0];
                double dy = event.getY() - lastMouseYForPan[0];
                offsetX[0] += dx;
                offsetY[0] += dy;
                lastMouseXForPan[0] = event.getX();
                lastMouseYForPan[0] = event.getY();
                needsFullRedraw[0] = true;
                mainRenderLoop.run();
                event.consume();
            }
        });

        createdCanvas.setOnMouseReleased((MouseEvent event) -> {
            if (lastMouseXForPan[0] != -1) {
                lastMouseXForPan[0] = -1;
                lastMouseYForPan[0] = -1;
                event.consume();
            }
        });

        /*
        createdCanvas.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            double oldScale = scale[0];
            double zoomFactor = (delta > 0) ? 1.05 : 1 / 1.05;
            scale[0] *= zoomFactor;
            if (scale[0] < MIN_SCALE) scale[0] = MIN_SCALE;
            if (scale[0] > MAX_SCALE) scale[0] = MAX_SCALE;
            double mouseX = event.getX();
            double mouseY = event.getY();
            offsetX[0] = mouseX - (mouseX - offsetX[0]) * (scale[0] / oldScale);
            offsetY[0] = mouseY - (mouseY - offsetY[0]) * (scale[0] / oldScale);
            needsFullRedraw[0] = true;
            mainRenderLoop.run();
            event.consume();
        });

         */
        createdCanvas.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            double oldScale = scale[0];

            // Координаты ЦЕНТРА ХОЛСТА (в экранных координатах)
            double centerX_screen = window.CANVAS_WIDTH / 2.0;
            double centerY_screen = window.CANVAS_HEIGHT / 2.0;

            // Мировые координаты точки под ЦЕНТРОМ ХОЛСТА ДО масштабирования
            double worldCenterX_before = (centerX_screen - offsetX[0]) / oldScale;
            double worldCenterY_before = (centerY_screen - offsetY[0]) / oldScale;

            // ... (ваш отладочный вывод можно адаптировать под worldCenterX/Y_before и _after)

            double zoomSensitivity = 0.05;
            double zoomFactor = (delta > 0) ? (1 + zoomSensitivity) : (1 / (1 + zoomSensitivity));

            scale[0] *= zoomFactor;
            if (scale[0] < MIN_SCALE) scale[0] = MIN_SCALE;
            if (scale[0] > MAX_SCALE) scale[0] = MAX_SCALE;

            if (Math.abs(scale[0] - oldScale) > 0.00001) {
                // Пересчитываем offsetX и offsetY, чтобы МИРОВАЯ точка (worldCenterX_before, worldCenterY_before)
                // осталась под ЭКРАННЫМИ координатами (centerX_screen, centerY_screen) при НОВОМ масштабе scale[0]
                offsetX[0] = centerX_screen - worldCenterX_before * scale[0];
                offsetY[0] = centerY_screen - worldCenterY_before * scale[0];
            }

            // ... (ваш отладочный вывод для worldCenterX/Y_after) ...

            needsFullRedraw[0] = true;
            mainRenderLoop.run();
            event.consume();
        });

        /*
        createdCanvas.setOnMouseMoved((MouseEvent event) -> {
            currentScreenMouseX[0] = event.getX();
            currentScreenMouseY[0] = event.getY();
            worldMouseX[0] = (currentScreenMouseX[0] - offsetX[0]) / scale[0];
            worldMouseY[0] = (currentScreenMouseY[0] - offsetY[0]) / scale[0];

            Flat previouslyHovered = currentlyHoveredFlatArray[0];
            currentlyHoveredFlatArray[0] = null;

            Set<Flat> flats = window.getActualFlats();
            if (flats != null && !flats.isEmpty()) {
                double flatWorldStartX = 10; // WORLD_MARGIN / 2.0;
                double currentFlatWorldTopY = -10; // -WORLD_MARGIN / 2.0;
                for (Flat flat : flats) {
                    if (flat == null) continue; double area = flat.getArea(); if (area <= 0) continue;
                    double sizeInWorld = Math.max(5 / scale[0], Math.sqrt(area) * 1.5);
                    double flatWorldRectY = currentFlatWorldTopY - sizeInWorld;
                    if (worldMouseX[0] >= flatWorldStartX && worldMouseX[0] <= flatWorldStartX + sizeInWorld &&
                            worldMouseY[0] >= flatWorldRectY && worldMouseY[0] <= flatWorldRectY + sizeInWorld) {
                        currentlyHoveredFlatArray[0] = flat;
                        break;
                    }
                    currentFlatWorldTopY -= (sizeInWorld + (15 / scale[0]));
                }
            }

            if (previouslyHovered != currentlyHoveredFlatArray[0]) {
                needsFullRedraw[0] = true;
            }
            needsHoverInfoRedraw[0] = true;
            mainRenderLoop.run();
            event.consume();
        });

         */
        createdCanvas.setOnMouseMoved((MouseEvent event) -> {
            currentScreenMouseX[0] = event.getX();
            currentScreenMouseY[0] = event.getY();
            worldMouseX[0] = (currentScreenMouseX[0] - offsetX[0]) / scale[0];
            worldMouseY[0] = (currentScreenMouseY[0] - offsetY[0]) / scale[0];

            Flat previouslyHovered = currentlyHoveredFlatArray[0];
            currentlyHoveredFlatArray[0] = null;

            Set<Flat> flats = window.getActualFlats();
            if (flats != null && !flats.isEmpty()) {
                // Убираем currentHitTestWorldX, currentHitTestWorldY_lineAboveFlat, worldSpacingForHitTest

                long currentTimeNanosForHitTest = (masterAnimationTimer != null && !activeAnimatingBatches.isEmpty()) ?
                        System.nanoTime() : 0;

                for (Flat flat : flats) {
                    if (flat == null) continue;
                    double area = flat.getArea();
                    if (area <= 0) continue;

                    // === Расчет РАЗМЕРА для hit-test (ИДЕНТИЧНО drawFlatsOnCanvas) ===
                    double baseSizeInWorld = Math.sqrt(area) * 1.5;
                    double minVisibleScreenSize = MIN_VISIBLE;
                    double targetSizeInWorld = Math.max(minVisibleScreenSize / scale[0], baseSizeInWorld);
                    double currentVisibleSizeForHitTest = targetSizeInWorld;
                    double animationProgressForThisFlat = 1.0;

                    if (currentTimeNanosForHitTest > 0) {
                        for (AnimatingBatch batch : activeAnimatingBatches) {
                            if (batch.flatIds.contains(flat.getId())) {
                                if (batch.isComplete) {
                                    animationProgressForThisFlat = 1.0;
                                } else {
                                    long elapsedNanos = currentTimeNanosForHitTest - batch.animationStartTimeNanos;
                                    double progress = (double) elapsedNanos / (APPEAR_ANIMATION_DURATION_MS * 1_000_000.0);
                                    if (progress >= 1.0) animationProgressForThisFlat = 1.0;
                                    else if (progress < 0.0) animationProgressForThisFlat = 0.0;
                                    else animationProgressForThisFlat = 1 - Math.pow(1 - progress, 3);
                                }
                                break;
                            }
                        }
                    }

                    if (animationProgressForThisFlat < 1.0) {
                        currentVisibleSizeForHitTest = targetSizeInWorld * (0.2 + 0.8 * animationProgressForThisFlat);
                        currentVisibleSizeForHitTest = Math.max(5.0 / scale[0], currentVisibleSizeForHitTest);
                    }
                    // === Конец Расчета РАЗМЕРА ===

                    // === Определение ПОЗИЦИИ для hit-test (ИДЕНТИЧНО drawFlatsOnCanvas) ===
                    // Предполагаем:
                    // flat.getCoordinates().getX() -> мировая X-координата ЛЕВОГО КРАЯ квадрата.
                    // flat.getCoordinates().getY() -> мировая Y-координата ВЕРХНЕГО КРАЯ квадрата (в вашей системе, где Y растет вверх).
                    double x_left_world_ht = flat.getCoordinates().getX();
                    double y_top_world_ht = flat.getCoordinates().getY(); // Это верхняя линия объекта в мировой системе Y-up

                    // Границы для проверки наведения:
                    // flatLeft: левый край объекта
                    // flatRight: правый край объекта
                    // flatTop_for_test: ВЕРХНИЙ край объекта в мировой системе Y-up
                    // flatBottom_for_test: НИЖНИЙ край объекта в мировой системе Y-up
                    double flatLeft = x_left_world_ht;
                    double flatRight = x_left_world_ht + currentVisibleSizeForHitTest;
                    double flatTop_for_test = y_top_world_ht; // Верхняя граница объекта
                    double flatBottom_for_test = y_top_world_ht - currentVisibleSizeForHitTest; // Нижняя граница объекта (т.к. Y растет вверх)


                    // Проверка наведения: worldMouseY[0] должен быть МЕЖДУ flatBottom_for_test и flatTop_for_test
                    if (worldMouseX[0] >= flatLeft && worldMouseX[0] <= flatRight &&
                            worldMouseY[0] >= flatBottom_for_test && worldMouseY[0] <= flatTop_for_test) { // Y-условие изменено
                        currentlyHoveredFlatArray[0] = flat;
                        break;
                    }
                }
            }

            if (previouslyHovered != currentlyHoveredFlatArray[0]) {
                needsFullRedraw[0] = true;
            } else if (currentlyHoveredFlatArray[0] != null) { // Если мышь движется над тем же объектом
                needsHoverInfoRedraw[0] = true;
            }

            mainRenderLoop.run();
            event.consume();
        });

        createdCanvas.setOnMouseExited((MouseEvent event) -> {
            currentScreenMouseX[0] = -1;
            currentScreenMouseY[0] = -1;
            if (currentlyHoveredFlatArray[0] != null) {
                currentlyHoveredFlatArray[0] = null;
                needsFullRedraw[0] = true;
            }
            needsHoverInfoRedraw[0] = true;
            mainRenderLoop.run();
            event.consume();
        });

        mainRenderLoop.run();
        return createdCanvas;
    }

    public void forceRedraw() {
        if (createdCanvas != null) {
            Object needsFullRedrawFlagObj = createdCanvas.getProperties().get("needsFullRedrawFlag");
            if (needsFullRedrawFlagObj instanceof boolean[]) {
                ((boolean[]) needsFullRedrawFlagObj)[0] = true; // Устанавливаем флаг
             //   System.out.println("AnimationManager: Flag needsFullRedraw set to true.");
            } else {
              //  System.err.println("AnimationManager: Could not find or cast needsFullRedrawFlag.");
                return;
            }

            Object loopObj = createdCanvas.getProperties().get("mainRenderLoop");
            if (loopObj instanceof Runnable) {
                Platform.runLater(() -> {
                    ((Runnable) loopObj).run();
                });
            } else {
           //     System.err.println("AnimationManager: Could not find or cast mainRenderLoop.");
            }
        } else {
            System.err.println("AnimationManager: Canvas is not initialized, cannot force redraw.");
        }
    }

    public void animateNewWaveOfFlatsAppearance(List<Flat> newFlats) {
        if (newFlats == null || newFlats.isEmpty()) {
            if (activeAnimatingBatches.isEmpty() && window.getCanvas() != null && window.getCanvas().getProperties().get("mainRenderLoop") instanceof Runnable) {
                Runnable needsFullRedrawSetter = (Runnable) window.getCanvas().getProperties().get("needsFullRedrawSetter");
                if(needsFullRedrawSetter != null) needsFullRedrawSetter.run();
                ((Runnable)window.getCanvas().getProperties().get("mainRenderLoop")).run();
            }
            return;
        }

        AnimatingBatch newBatch = new AnimatingBatch(newFlats, System.nanoTime());
        activeAnimatingBatches.add(newBatch);
     //   System.out.println("Новая волна анимации добавлена для " + newBatch.flatIds.size() + " квартир. Всего активных волн: " + activeAnimatingBatches.size());

        if (masterAnimationTimer == null) {
            masterAnimationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    Iterator<AnimatingBatch> iterator = activeAnimatingBatches.iterator();
                    boolean anyBatchStillAnimating = false;
                    boolean animationStateChangedThisFrame = false;

                    while(iterator.hasNext()){
                        AnimatingBatch batch = iterator.next();
                        // Сохраняем состояние isComplete до вызова getCurrentProgress,
                        // так как getCurrentProgress может изменить isComplete.
                        boolean wasCompleteBeforeUpdate = batch.isComplete;
                        double progress = batch.getCurrentProgress(now, APPEAR_ANIMATION_DURATION_MS); // Обновляет isComplete внутри

                        if (!wasCompleteBeforeUpdate && progress < 1.0) { // Если не был завершен и все еще не завершен, но прогресс идет
                            animationStateChangedThisFrame = true;
                        } else if (!wasCompleteBeforeUpdate && batch.isComplete) { // Если только что завершился
                            animationStateChangedThisFrame = true;
                        }


                        if(batch.isComplete){
                            iterator.remove();
                          //  System.out.println("Волна анимации завершена. Осталось: " + activeAnimatingBatches.size());
                            animationStateChangedThisFrame = true;
                        } else {
                            anyBatchStillAnimating = true;
                        }
                    }

                    Canvas canvas = window.getCanvas(); // Получаем текущий Canvas из MainWindow

                    if (canvas != null && (anyBatchStillAnimating || animationStateChangedThisFrame)) {
                        Object needsFullRedrawFlagObj = canvas.getProperties().get("needsFullRedrawFlag");
                        if (needsFullRedrawFlagObj instanceof boolean[]) {
                            ((boolean[]) needsFullRedrawFlagObj)[0] = true; // Устанавливаем флаг
                        }

                        Object loopObj = canvas.getProperties().get("mainRenderLoop");
                        if (loopObj instanceof Runnable) {
                            ((Runnable) loopObj).run(); // Запускаем отрисовку
                        }
                    }

                    if (activeAnimatingBatches.isEmpty() && !anyBatchStillAnimating) {
                        this.stop();
                        masterAnimationTimer = null;
                     //   System.out.println("Все волны завершены, мастер-таймер остановлен.");
                        // Финальная перерисовка после остановки таймера
                        if (canvas != null) {
                            Object needsFullRedrawFlagObj = canvas.getProperties().get("needsFullRedrawFlag");
                            if (needsFullRedrawFlagObj instanceof boolean[]) {
                                ((boolean[]) needsFullRedrawFlagObj)[0] = true;
                            }
                            Object loopObj = canvas.getProperties().get("mainRenderLoop");
                            if (loopObj instanceof Runnable) {
                                ((Runnable) loopObj).run();
                            }
                        }
                    }
                }
            };
            masterAnimationTimer.start();
        }
    }

    private Color generateColorFromString(String seedString) {
        if (seedString == null || seedString.isEmpty()) {
            return Color.LIGHTGRAY;
        }

        int hashCode = seedString.hashCode();

        // Оттенок (Hue): от 0 до 359 градусов.
        double hue = Math.abs(hashCode % 360.0);
        double saturation = (((hashCode >> 8) & 0xFF) / 255.0) * 0.4 + 0.6; // Диапазон [0.6, 1.0]

        double brightness = (((hashCode >> 16) & 0xFF) / 255.0) * 0.25 + 0.75; // Диапазон [0.75, 1.0]

        saturation = Math.max(0.6, Math.min(1.0, saturation));
        brightness = Math.max(0.75, Math.min(1.0, brightness));

        Color result = Color.hsb(hue, saturation, brightness);
        return result;
    }

    private static class AnimatingBatch {
        final Set<Long> flatIds = new HashSet<>();      // ID квартир в этой пачке
        final long animationStartTimeNanos;             // Время старта анимации этой пачки
        boolean isComplete = false;                   // Завершена ли анимация для этой пачки

        AnimatingBatch(List<Flat> flatsInBatch, long startTimeNanos) {
            this.animationStartTimeNanos = startTimeNanos;
            if (flatsInBatch != null) {
                for (Flat flat : flatsInBatch) {
                    if (flat != null) this.flatIds.add(flat.getId()); // Предполагаем Flat::getId()
                }
            }
        }

        double getCurrentProgress(long currentTimeNanos, long animationDurationMs) {
            if (isComplete) {
                return 1.0;
            }
            long elapsedNanos = currentTimeNanos - this.animationStartTimeNanos;
            double progress = (double) elapsedNanos / (animationDurationMs * 1_000_000.0);

            if (progress >= 1.0) {
                this.isComplete = true; // Помечаем как завершенную
                return 1.0;
            }
            // Применяем функцию плавности
            return 1 - Math.pow(1 - progress, 3); // ease-out cubic
        }
    }


}
