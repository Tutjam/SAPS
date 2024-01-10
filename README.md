# SAPS
Single Anti Phishing Service - system do odfiltrowywania wiadomości zawierających niebezpieczne linki

Rozwiązanie zostało zaimplementowane jako REST API. Zastanawiałem się nad implementacją systemu publish/subscribe na bazie kafki, gdzie nasłuchiwałbym na określonymi topic’u, consumer przetwarzałby otrzymane wiadomości i jeżeli byłyby bezpieczne, to odsyłałby je na innym topicu do brokera, jednak brak znajomości i doświadczenia z tą technologią skłonił mnie do decyzji związanej z wyborem REST API.

W zasadzie składa się z jednego endpoint’a. Dosyć nietypowego, bo w niektórych case’ach zwraca jeden model danych(wiadomość), w innych zwraca inny model (subskrypcja). Zostało to rozwiązane w ten sposób, aby ułatwić komunikację. W przypadku, gdy zaktualizowana zostanie subskrypcja, zwraca subskrypcje, w przypadku, gdy wiadomość jest bezpieczna - zwraca wiadomość. 
Klient w wypadku, gdy serwis zwróci wiadomość wie, że może przekazać ją dalej, tak samo, jeżeli subskrypcja zostanie zaktualizowana. Początkowo endpoint filtrowania wiadomości oraz subskrypcji zaimplementowałem oddzielnie, jednak uznałem, że serwis, który będzie korzystał z filtra niekoniecznie musi znać numer/identyfikator użytkownika, który odpowiada za aktualizacje subskrypcji - 
w takim wypadku nie wiedziałby, kiedy wysłać wiadomość na endpoint za to odpowiadający. Do wymiany danych użyto formatu JSON, ze względu na jego czytelność oraz łatwość obsługi z wykorzystaniem Play Framework.
Dla ułatwienia numer odpowiedzialny za aktualizacje subskrypcji jest stałą zapisaną w obiekcie SubscriptionManager.  Warstwa DAO pracuje nie na bazie danych, a na mutowalnej liście (dane są generowane przy każdym restarcie aplikacji na nowo, z losowymi wartościami aktywności subskrypcji). W celu aktualizacji subskrypcji modyfikowane są obiekty klasy Subscription - dlatego jest wykorzystana zwykła klasa, 
a nie case class w tym wypadku. Wszystkie zabiegi opisane w tym paragrafie związane są z ułatwieniem implementacji ze względu na brak połączenia z realną bazą danych - braku potrzeby jej konfiguracji, dodawania narzędzi do tego celu.
W celu zmniejszenia kosztów klienta zastosowane zostało cache, które przechowuje niebezpieczne linki. Cache jest mutowalną listą w obiekcie. Nie ma ustawionego schedulera, który by tą listę czyścił, więc jest czyszczona tylko przy restarcie aplikacji. Podejście dla ułatwienia. Jeżeli aplikacja będzie stała stosunkowo długo, cache może być nieaktualne i prowadzić do błędnego odrzucania bezpiecznych wiadomości.
W przypadku, gdy serwis do weryfikacji URLi nie odpowie responsem, który uda się sparsować przyjmujemy z góry, że wiadomość jest bezpieczna. Więcej opcji zostało opisane w komenatrzu w metodzie związanej z tą sytuacją.

W ważniejszych miejscach dodane zostały logi, które informują o przebiegu przetwarzania wiadomości.
Do wyciągnięcia URL'i z wiadomości została wykorzystana biblioteka ||org.nibor.autolink" % "autolink" % „0.11.0”|| - zakładam, że biblioteka jest nieomylna i wychwyci wszystkie URL’e wewnątrz wiadomości.
Do odpytania zewnętrznego api został utworzony oddzielny serwis. W serwisie wykonywany jest request pod endpoint wskazany w treści zadania. Do wykonania żądania wykorzystywany jest WSClient z paczki Play’a
Zaimplementowane zostało kilka testów dla kontrolera oraz serwisów.

Endpoint /messages
Jako body przyjmuje JSON'a w formacie
{
    "sender": "234100200300",
    "recipient": "48700800999",
    "message": "Dzień dobry. W związku z audytem nadzór finansowy w naszym banku proszą o potwierdzanie danych pod adresem:"
}
W przypadku, jeżeli wiadomość jest phishingowa zwraca status 204 NoContent.
Jeżeli wiadomość jest bezpieczna - zwraca status 200 oraz wiadomość w takim samym formacie, w jakim otrzymał.
Jeżeli wiadomość jest o treści START lub STOP - zwraca status 200 oraz zaktualizowaną subskrypcję w formacie
{
„userId": "234100200300",
„isActive”: true,

}
Aplikacja została zbudowana w oparciu o Play Framework.
Java 11, Scala 2.13.12,
Play 2.9.0, sbt 1.5.0
