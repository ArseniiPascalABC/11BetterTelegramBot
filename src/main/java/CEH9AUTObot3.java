import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CEH9AUTObot3 extends TelegramLongPollingBot {
    String botName;
    String botToken;

    String sql = "";
    String idName = "";
    String firstName;
    String idAuto;
    String dayNachala;
    String timeNachala;
    String naSkolko;
    int forDateAndTime = 0;

    int countMess = -1;
    String[][] allUserMessages = new String[100][2];
    String messageOtUsera;
    SendMessage messageToUser;
    SendPhoto photoToUser = new SendPhoto();
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    public CEH9AUTObot3(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5438/ceh9bot", "postgres", "postgres");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            countMess++;
            firstName = update.getMessage().getFrom().getFirstName();
            idName = String.valueOf(update.getMessage().getFrom().getId());
            String chatId = update.getMessage().getChatId().toString();
            messageOtUsera = update.getMessage().getText();
            allUserMessages[0][0] = firstName;
            allUserMessages[0][1] = idName;
            allUserMessages[1][countMess] = messageOtUsera;
            messageToUser = new SendMessage();
            messageToUser.setChatId(chatId);
            try {
                switch (messageOtUsera) {
                    case "/start" -> {
                        defaultKeyboard();
                        messageToUser.setChatId(chatId);
                        messageToUser.setText("Добро пожаловать " + firstName + ". Что хотите сделать?");
                        execute(messageToUser);
                    }
                    case "/5830283080:AAE9awKU8SaL76vQfa-QoxWct-vqNocfVF4" -> {
                        messageToUser.setText("Админ ку");
                        defaultKeyboard();
                        execute(messageToUser);
                    }
                    default -> {
                        messageToUser.setText("Неправильный формат команды");
                        execute(messageToUser);
                    }
                }

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }


        if (update.hasCallbackQuery()) {
            String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();

                switch (callbackData) {

                    case "allCatalog" -> {
                        replyKey(callbackQuery);
                        messageToUser.setText("Каталог:");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        queryy("SELECT * FROM catalog", chatId);
                    }

                    case "filterCatalog" -> {
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                        List<InlineKeyboardButton> row1 = new ArrayList<>();
                        row1.add(InlineKeyboardButton.builder()
                                .text("Цвет")
                                .callbackData("color")
                                .build());
                        row1.add(InlineKeyboardButton.builder()
                                .text("Кол-во мест")
                                .callbackData("countSeats")
                                .build());
                        keyboard.add(row1);

                        List<InlineKeyboardButton> row2 = new ArrayList<>();
                        row2.add(InlineKeyboardButton.builder()
                                .text("Кузов")
                                .callbackData("kuzov")
                                .build());
                        row2.add(InlineKeyboardButton.builder()
                                .text("Наличие")
                                .callbackData("INSTOCK")
                                .build());
                        keyboard.add(row2);
                        inlineKeyboardMarkup.setKeyboard(keyboard);

                        messageToUser.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                        messageToUser.setReplyMarkup(inlineKeyboardMarkup);
                        messageToUser.setText("Какой параметр фильтровать будем?");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                    case "allOrders" -> {
                        String sql = "SELECT * FROM \"order\" WHERE \"client_id\" = ?";
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            statement.setInt(1, Integer.parseInt(idName));
                            ResultSet resultSet = statement.executeQuery();
                            replyKey(callbackQuery);
                            boolean hasOrders = false;
                            while (resultSet.next()) {
                                hasOrders = true;
                                int clientId = resultSet.getInt("client_id");
                                int orderId = resultSet.getInt("id заказа");
                                int autoId = resultSet.getInt("id авто");
                                int dayCountId = resultSet.getInt("количество дней");
                                String dayId = String.valueOf(resultSet.getDate("День начала аренды"));
                                Time timeId = resultSet.getTime("Время начала аренды");
                                String message ="Имя: " + firstName + "\nid клиента: " + clientId + "\nid заказа: " + orderId + "\nid авто: " + autoId + "\nКоличество дней: " + dayCountId + "\nДень начала аренды: " + dayId + "\nВремя начала аренды: " + timeId;
                                messageToUser.setText(message);
                                execute(messageToUser);
                                sql = "SELECT * FROM \"catalog\" WHERE \"id авто\" = ?";
                                try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                                    statement2.setInt(1,autoId);
                                    ResultSet resultSet2 = statement2.executeQuery();
                                    if (resultSet2.next()){
                                        String name = resultSet2.getString("название");
                                        String color = resultSet2.getString("количество_мест");
                                        String cuzov = resultSet2.getString("кузов");
                                        String price = resultSet2.getString("цена");
//                                        String STOCK = resultSet2.getString("INSTOCK");
                                        message =  "ID авто: " + autoId + "\nНазвание: " + name + "\nЦвет: " + color + "\nКузов: " + cuzov + "\nЦена: " + price;
                                        messageToUser.setText(message);
                                        InputFile photo = new InputFile(new File("C:\\Users\\senya\\IdeaProjects\\SE_LAB11BOTYARA\\src\\main\\resources\\" + autoId + ".jpg"));
                                        photoToUser = new SendPhoto();
                                        photoToUser.setChatId(chatId);
                                        photoToUser.setPhoto(photo);
                                        execute(photoToUser);
                                        defaultKeyboard();
                                        execute(messageToUser);
                                    }
                                }
                            }
                            if (!hasOrders) {
                                messageToUser.setText("Заказов не найдено(");
                                execute(messageToUser);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }



                    case "help" -> {
                        replyKey(callbackQuery);
                        messageToUser.setText("По возникшим вопросам просьба обращаться -> @CEH9CEH9");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    case "confirmOrder" -> {
                        forDateAndTime = 0;
                        messageToUser.setText("Выберите день:");
                        LocalDate currentDate = LocalDate.now();

                        // Создаем список кнопок
                        List<InlineKeyboardButton> buttons = new ArrayList<>();

                        // Добавляем кнопки с датами на 6 дней вперед
                        for (int i = 0; i < 6; i++) {
                            LocalDate date = currentDate.plusDays(i + 1);
                            String buttonText = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                            InlineKeyboardButton button = new InlineKeyboardButton();
                            button.setText(buttonText);
                            button.setCallbackData(buttonText);
                            buttons.add(button);
                        }
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                        for (int i = 0; i < buttons.size(); i += 2) {
                            List<InlineKeyboardButton> row = new ArrayList<>();
                            row.add(buttons.get(i));

                            if (i + 1 < buttons.size()) {
                                row.add(buttons.get(i + 1));
                            }

                            keyboard.add(row);
                        }

                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                        markup.setKeyboard(keyboard);
                        messageToUser.setReplyMarkup(markup);
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    case "backToBack" -> {
                        defaultKeyboard();
                        messageToUser.setChatId(chatId);
                        messageToUser.setText("Что будем делать " + firstName + "?");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    case "color"->{
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                        List<InlineKeyboardButton> row1 = new ArrayList<>();
                        row1.add(InlineKeyboardButton.builder()
                                .text("Черный")
                                .callbackData("black")
                                .build());
                        row1.add(InlineKeyboardButton.builder()
                                .text("Белый")
                                .callbackData("white")
                                .build());
                        keyboard.add(row1);

                        List<InlineKeyboardButton> row2 = new ArrayList<>();
                        row2.add(InlineKeyboardButton.builder()
                                .text("Серый")
                                .callbackData("grey")
                                .build());
                        row2.add(InlineKeyboardButton.builder()
                                .text("Другой")
                                .callbackData("another")
                                .build());
                        keyboard.add(row2);
                        inlineKeyboardMarkup.setKeyboard(keyboard);

                        messageToUser.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                        messageToUser.setReplyMarkup(inlineKeyboardMarkup);
                        messageToUser.setText("Какой?");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    case "countSeats"->{
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                        List<InlineKeyboardButton> row1 = new ArrayList<>();
                        row1.add(InlineKeyboardButton.builder()
                                .text("1-2")
                                .callbackData("one-two")
                                .build());
                        row1.add(InlineKeyboardButton.builder()
                                .text("3-4")
                                .callbackData("three-four")
                                .build());
                        keyboard.add(row1);

                        List<InlineKeyboardButton> row2 = new ArrayList<>();
                        row2.add(InlineKeyboardButton.builder()
                                .text("5+")
                                .callbackData("five or more")
                                .build());
                        keyboard.add(row2);
                        inlineKeyboardMarkup.setKeyboard(keyboard);

                        messageToUser.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                        messageToUser.setReplyMarkup(inlineKeyboardMarkup);
                        messageToUser.setText("Сколько?");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    case "kuzov"->{
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                        List<InlineKeyboardButton> row1 = new ArrayList<>();
                        row1.add(InlineKeyboardButton.builder()
                                .text("Седан")
                                .callbackData("Sedan")
                                .build());
                        row1.add(InlineKeyboardButton.builder()
                                .text("Универсал")
                                .callbackData("Universal")
                                .build());
                        keyboard.add(row1);

                        List<InlineKeyboardButton> row2 = new ArrayList<>();
                        row2.add(InlineKeyboardButton.builder()
                                .text("Хэтчбек")
                                .callbackData("HatchBack")
                                .build());
                        row2.add(InlineKeyboardButton.builder()
                                .text("Другие")
                                .callbackData("anothers")
                                .build());
                        keyboard.add(row2);
                        inlineKeyboardMarkup.setKeyboard(keyboard);

                        messageToUser.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                        messageToUser.setReplyMarkup(inlineKeyboardMarkup);
                        messageToUser.setText("Какой?");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    case "INSTOCK"->{
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                        List<InlineKeyboardButton> row1 = new ArrayList<>();
                        row1.add(InlineKeyboardButton.builder()
                                .text("IN STOCK")
                                .callbackData("IN STOCK")
                                .build());
                        row1.add(InlineKeyboardButton.builder()
                                .text("OUT OF STOCK")
                                .callbackData("OUT OF STOCK")
                                .build());
                        keyboard.add(row1);
                        inlineKeyboardMarkup.setKeyboard(keyboard);

                        messageToUser.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                        messageToUser.setReplyMarkup(inlineKeyboardMarkup);
                        messageToUser.setText("Какие?");
                        try {
                            execute(messageToUser);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    case "black" -> queryy("SELECT * FROM catalog WHERE цвет = 'Черный';", chatId);
                    case "white" -> queryy("SELECT * FROM catalog WHERE цвет = 'Белый';", chatId);
                    case "grey" -> queryy("SELECT * FROM catalog WHERE цвет = 'Серый';", chatId);
                    case "another" ->
                            queryy("SELECT * FROM catalog WHERE цвет NOT IN ('Черный', 'Белый', 'Серый');", chatId);
                    case "one-two" -> queryy("SELECT * FROM catalog WHERE \"количество_мест\" IN (1, 2);", chatId);
                    case "three-four" -> queryy("SELECT * FROM catalog WHERE \"количество_мест\" IN (3, 4);", chatId);
                    case "five or more" -> queryy("SELECT * FROM catalog WHERE \"количество_мест\" > 4;", chatId);
                    case "Sedan" -> queryy("SELECT * FROM catalog WHERE кузов = 'Седан';", chatId);
                    case "Universal" -> queryy("SELECT * FROM catalog WHERE кузов = 'Универсал';", chatId);
                    case "Hatchback" -> queryy("SELECT * FROM catalog WHERE кузов = 'Хэтчбек';", chatId);
                    case "anothers" ->
                            queryy("SELECT * FROM catalog WHERE кузов NOT IN ('Седан', 'Хэтчбек', 'Универсал');", chatId);
                    case "IN STOCK" -> queryy("SELECT * FROM catalog WHERE \"INSTOCK\" = true;", chatId);
                    case "OUT OF STOCK" -> queryy("SELECT * FROM catalog WHERE \"INSTOCK\" = false;", chatId);
                    default -> {
                        if (forDateAndTime == 0){
                            dayNachala = update.getCallbackQuery().getData();
                            System.out.println("День " + dayNachala);
                            messageToUser.setText("Выберите время начала аренды: ");
                            // Создаем список кнопок
                            List<InlineKeyboardButton> buttons = new ArrayList<>();

                            // Добавляем кнопки с временем с интервалом в один час
                            LocalTime startTime = LocalTime.of(10, 0);
                            LocalTime endTime = LocalTime.of(21, 0);
                            while (startTime.isBefore(endTime)) {
                                String buttonText = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                                InlineKeyboardButton button = new InlineKeyboardButton();
                                button.setText(buttonText);
                                button.setCallbackData(buttonText);
                                buttons.add(button);

                                startTime = startTime.plusHours(1);
                            }

                            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                            for (int i = 0; i < buttons.size(); i += 2) {
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                row.add(buttons.get(i));

                                if (i + 1 < buttons.size()) {
                                    row.add(buttons.get(i + 1));
                                }

                                keyboard.add(row);
                            }

                            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                            markup.setKeyboard(keyboard);
                            messageToUser.setReplyMarkup(markup);
                            try {
                                execute(messageToUser);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            forDateAndTime++;
                        }
                        else if(forDateAndTime == 1){
                            timeNachala = update.getCallbackQuery().getData();
                            System.out.println("Время " + timeNachala);
                            messageToUser.setText("Выберите количество дней: ");
                            // Создаем список кнопок
                            List<InlineKeyboardButton> buttons = new ArrayList<>();

                            // Добавляем кнопки с количеством дней от 1 до 14
                            for (int i = 1; i <= 14; i++) {
                                String buttonText = String.valueOf(i);

                                InlineKeyboardButton button = new InlineKeyboardButton();
                                button.setText(buttonText);
                                button.setCallbackData(buttonText);
                                buttons.add(button);
                            }

                            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                            for (int i = 0; i < buttons.size(); i += 2) {
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                row.add(buttons.get(i));

                                if (i + 1 < buttons.size()) {
                                    row.add(buttons.get(i + 1));
                                }

                                keyboard.add(row);
                            }

                            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                            markup.setKeyboard(keyboard);
                            messageToUser.setReplyMarkup(markup);

                            try {
                                execute(messageToUser);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            forDateAndTime++;
                        }else if(forDateAndTime == 2){
                            naSkolko = update.getCallbackQuery().getData();

                            messageToUser.setText("Выберите ID авто: ");

                            List<Integer> idList = new ArrayList<>();

                            try (PreparedStatement statement = connection.prepareStatement("SELECT \"id авто\" FROM \"catalog\" WHERE \"INSTOCK\" = true")) {

                                ResultSet resultSet = statement.executeQuery();

                                while (resultSet.next()) {
                                    int id = resultSet.getInt("id авто");
                                    idList.add(id);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }

                            List<InlineKeyboardButton> buttons = new ArrayList<>();


                            for (int id : idList) {
                                InlineKeyboardButton button = InlineKeyboardButton.builder()
                                        .text(String.valueOf(id))
                                        .callbackData(String.valueOf(id))
                                        .build();
                                buttons.add(button);
                            }

                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            List<InlineKeyboardButton> row = new ArrayList<>();

                            for (InlineKeyboardButton button : buttons) {
                                row.add(button);

                                if (row.size() == 2) {
                                    rows.add(row);
                                    row = new ArrayList<>();
                                }
                            }


                            if (!row.isEmpty()) {
                                rows.add(row);
                            }
                            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
                            messageToUser.setReplyMarkup(markup);
                            forDateAndTime++;
                            try {
                                execute(messageToUser);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("На сколько" + naSkolko);
                        }
                        else if(forDateAndTime == 3){
                            idAuto = update.getCallbackQuery().getData();

                            String selectSql = "SELECT COUNT(*) FROM \"client\" WHERE \"id\" = ?";
                            String insertSql = "INSERT INTO \"client\" (\"id\", \"имя\") VALUES (?, ?)";

                            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                                 PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

                                // Проверяем наличие пользователя в таблице
                                selectStatement.setInt(1, Integer.parseInt(idName));
                                ResultSet resultSet = selectStatement.executeQuery();
                                resultSet.next();
                                int count = resultSet.getInt(1);

                                if (count == 0) {
                                    // Пользователь не найден, выполняем вставку
                                    insertStatement.setInt(1, Integer.parseInt(idName));
                                    insertStatement.setString(2, firstName);
                                    insertStatement.executeUpdate();
                                    System.out.println("Пользователь добавлен");
                                } else {
                                    System.out.println("Пользователь уже существует");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }

                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                            LocalDate date = LocalDate.parse(dayNachala, inputFormatter);

                            DateTimeFormatter inputFormatter2 = DateTimeFormatter.ofPattern("HH:mm");

                            // Преобразование строки в объект LocalTime
                            LocalTime time = LocalTime.parse(timeNachala, inputFormatter2);

                            sql = "INSERT INTO \"order\" (\"client_id\", \"id авто\", \"количество дней\", \"День начала аренды\", \"Время начала аренды\") VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                                statement.setInt(1, Integer.parseInt(idName));
                                statement.setInt(2, Integer.parseInt(idAuto));
                                statement.setInt(3, Integer.parseInt(naSkolko));
                                statement.setDate(4, Date.valueOf(date));
                                statement.setTime(5, Time.valueOf(time));

                                int rowsAffected = statement.executeUpdate();
                                if (rowsAffected > 0) {

                                    String updateSql = "UPDATE \"catalog\" SET \"INSTOCK\" = false WHERE \"id авто\" = ?";
                                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                                        updateStatement.setInt(1, Integer.parseInt(idAuto));
                                        updateStatement.executeUpdate();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        throw new RuntimeException(e);
                                    }

                                    sql = "SELECT \"цена\" FROM \"catalog\" WHERE \"id авто\" = ?";

                                    try(PreparedStatement statement2 = connection.prepareStatement(sql)){
                                        statement2.setInt(1,Integer.parseInt(idAuto));
                                        ResultSet resultSet = statement2.executeQuery();
                                        if(resultSet.next()){
                                            int price = resultSet.getInt("цена");
                                            price *= Integer.parseInt(naSkolko);
                                            messageToUser.setText("Аренда прошла успешно, к оплате будет: " + price);
                                            defaultKeyboard();
                                            execute(messageToUser);
                                            forDateAndTime++;
                                        }
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    messageToUser.setText("Произошла ошибка, для дополнительной информации свяжитесь с нами в разделе help");
                                    execute(messageToUser);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            messageToUser.setText("Просим прощения, просим нажать сюда /start");
                            try {
                                execute(messageToUser);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }

        }

    public void queryy(String query, String chatId) {
        try {
            // Создание объекта Statement для выполнения запроса
            statement = connection.createStatement();

            // Выполнение запроса выборки
            resultSet = statement.executeQuery(query);

            // Проверка наличия результатов
            if (resultSet.next()) {
                // Обработка результатов выборки
                do {
                    // Получение значений из текущей строки результата
                    int id = resultSet.getInt("id авто");
                    String name = resultSet.getString("название");
                    String color = resultSet.getString("цвет");
                    int countSeat = resultSet.getInt("количество_мест");
                    String cuzov = resultSet.getString("кузов");
                    int pricePH = resultSet.getInt("цена");
                    boolean INSTOCK = resultSet.getBoolean("INSTOCK");
                    String message = "id Авто: " + id + "\nМодель: " + name + "\nЦвет: " + color + "\nКоличество мест: " + countSeat + "\nКузов: " + cuzov + "\nЦена в час: " + pricePH + "\nНаличие: " + INSTOCK;
                    messageToUser.setText(message);
                    InputFile photo = new InputFile(new File("C:\\Users\\senya\\IdeaProjects\\SE_LAB11BOTYARA\\src\\main\\resources\\" + id + ".jpg"));
                    photoToUser = new SendPhoto();
                    photoToUser.setChatId(chatId);
                    photoToUser.setPhoto(photo);

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                    List<InlineKeyboardButton> row1 = new ArrayList<>();
                    row1.add(InlineKeyboardButton.builder()
                            .text("Оформить заказ")
                            .callbackData("confirmOrder")
                            .build());
                    row1.add(InlineKeyboardButton.builder()
                            .text("Назад")
                            .callbackData("backToBack")
                            .build());
                    keyboard.add(row1);
                    inlineKeyboardMarkup.setKeyboard(keyboard);
                    messageToUser.setReplyMarkup(inlineKeyboardMarkup);

                    execute(photoToUser);
                    execute(messageToUser);
                } while (resultSet.next());
            } else {
                // Результаты не найдены, выполните соответствующие действия
                String message = "Результаты не найдены.";
                messageToUser.setText(message);
                defaultKeyboard();
                execute(messageToUser);
            }
        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void replyKey(CallbackQuery callbackQuery){
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        replyKeyboardRemove.setSelective(false);
        messageToUser.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        messageToUser.setReplyMarkup(replyKeyboardRemove);
    }
    public void defaultKeyboard (){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("Весь каталог")
                .callbackData("allCatalog")
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text("Заказы")
                .callbackData("allOrders")
                .build());
        keyboard.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("Фильтр каталога")
                .callbackData("filterCatalog")
                .build());
        row2.add(InlineKeyboardButton.builder()
                .text("Помощь")
                .callbackData("help")
                .build());
        keyboard.add(row2);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        messageToUser.setReplyMarkup(inlineKeyboardMarkup);
    }
}