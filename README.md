  Консольный вариант приложения для создания голосования в разделах. 
  Изначально создала простой вариант в классе VotingService
  Потом добавила клиент-серверную часть, но т.к.  для меня это первый опыт работы с библиотекой Netty,
  не хватило времени, чтобы все оформить, добавить логирование и покрыть все тестами
  
  ClientMain - точка входа для клиента
  login -u=username – подключиться к серверу с указанным именем
  пользователя (все остальные команды доступны только после выполнения
  login)

  ◦ create topic -n=<topic> - создает новый раздел c уникальным именем
  заданным в параметре -n

  ◦ view - показывает список уже созданных разделов в формате: <topic (votes
  in topic=<count>)>

  ▪ опциональный параметр -t=<topic> - в этом случае команда показывает
  список голосований в конкретном разделе

  ◦ create vote -t=<topic> - запускает создание нового голосования в разделе
  указанном в параметре -t

  Для создания голосования (команда create vote -t=<topic>) нужно
  последовательно запросить у пользователя:
  • название (уникальное имя)

  • тему голосования (описание)

  • количество вариантов ответа

  • варианты ответа

  ◦ view -t=<topic> -v=<vote> - отображает информацию по голосованию

  ▪ тема голосования

  ▪ варианты ответа и количество пользователей выбравших данный
  вариант

  ◦ vote -t=<topic> -v=<vote> - запускает выбор ответа в голосовании для
  текущего пользователя

  Для этого приложение должно

  ▪ вывести варианты ответа для данного голосования

  ▪ запросить у пользователя выбор ответа

  ◦ delete -t=topic -v=<vote> - удалить голосование с именем <vote> из <topic>
  (удалить может только пользователь его создавший)

  ◦ exit - завершение работы программы

  ServerMain - точка входа для сервера

  ◦ load <filename> - загрузка данных из файла

  ◦ save <filename> – сохранение в файл всех созданных разделов и
  принадлежащим им голосований + их результатов (в любом удобном
  формате).

  ◦ exit - завершение работы программы

  Для VotingService
  login -u=username - Войти под именем

  create topic -n=name - Создать раздел

  create vote -t=topic - Создать голосование

  view - Список разделов

  view -t=topic - Список голосований в разделе

  view -t=topic -v=vote - Просмотр голосования

  vote -t=topic -v=vote - Проголосовать

  delete -t=topic -v=vote - Удалить голосование

  save - Сохранить данные

  exit - Выход


Созданный набор разделов, голосований и результаты голосования хранятся в json формате (файл в resources/VoteAppData.json)
После старта приложения загружаются созданные ранее данные из файла 
