###Tuntikirjanpito
Päivät | Tunnit | Kuvaus
---------------- | -------- | --------------
27.07.2016 | 2h | Evaluaatiofunktion ja maximin-algoritmin suunnittelua
28.07.2016 | 5h | Evaluaatiofunktion ja maximin-algoritmin toteutus lähes valmis, aloitettu tekoälyä vastaan pelaamisen lisääminen shakkipeliin.
31.07.2016 | 6h | Tekoälyä  vastaan pelaaminen ja tekoäly vs tekoäly toimii, korjattu bugi maxi-min-algoritmissa. Jostain syystä sotilaat voivat liikkua 2 ruutua aina, kun pelissä mukana AI.
02.08.2016 | 5h | Korjattu kaikki löytämäni bugit, nopeutettu toimintaa alfa/beeta-karsinnalla.
03.08.2016 | 6h | Muutettu nappuloiden materiaaliarvoja paremmiksi, lisätty evaluaatioon liikkuvuusarvo ja aloitettu nappuloiden sijaintikohtaisen arvostelun lisäys.
06.08.2016 | 4h | Päivitetty aiheenmäärittelyä ja siinä yhteydessä pohdittu vielä tarkemmin eri arviontimenetelmien aikavaativuuksia.
09.08.2016 | 2h | Päivitetty testit ajan tasalle ja kirjoitettu viikkoraportti.
09.08.2016 | 3h | Sijaintikohtaiset arvot lisätty jokaiselle nappulaluokalle.
10.08.2016 | 6h | Lisätty alkeelliset toteutukset MyHashMapille, MyHashSetille ja MyArrayListille. Aloitettu testien kirjoittaminen niille.
11.08.2016 | 8h | Korjattu MyHashSetin toteutus hyödyntämään MyStackia ja MyLinkedListiä parempaa toimimista varten. Lisätty iteraattorille toteutus MyHashSettiin ja MyLinkedListiin. Lisätty testit kaikille omille tietorakenteille, joilla muitakin metodeita kuin vain getterit. Laskettu tarkka aikavaativuss ja lisätty se Unicodella aiheenmäärittelyyn.
12.08.2016 | 3h | Lisätty javadoc kommentit julkisille metodeille omiin tietorakenteisiin ja korjattu bugi, jonka vuoksi tekoäly ei huomioinut sotilaiden korotusta.
13.08.2016 | 4h | Vaihdettu tekoälyn algoritmi minmaxista negamaxiin, jotta copypaste vähenisi.
14.08.2016 | 5h | Lisätty transpositiotaulu, jonka avulla vältetään saman tilanteen uudelleen arvioiminen. Lisäksi otetaan talteen edellisen iteraation parhaat siirrot ja yritetään niitä ensimmäisenä, jotta alfa-beeta-karsinnan teho paranisi. (Yhteensä keskimääräinen vuoroon kulunut aikatippui noin 15s:ta noin 7s:iin syvyydellä 3).
15.08.2016 | 4h | Lisätty shakkiin sääntö tilanteen kolmannesta toisinnosta ja luotu metodi undoMove, joka kumoaa siirron tehokkaammin kuin aikaisempi reverOldSituation.
16.08.2016 | 2h | Lisätty iteratiivinen syventäminen ja aloitettu rakenteen muuttaminen sellaiseksi, että tekoäly voi huomioida tilanteen toistokerrat
17.08.2016 | 5h | Iteratiivisen syvennyksen aikana otetaan ylös paras siirtoketju annetullle syvyydelle asti. Peli loppuu myös tekoälyn siirron jälkee, jos tilanne toistuu kolmatta kertaa. Aloitettu korjaamaan tilanteiden Zobrist-hashauksessa ilmenneitä ongelmia.
20.08.2016 | 6h | Tallennetaan dynaamiseen muistiin paras siirtoketju edrellistä siirtoa laskettaessa ja hyödynnetään sitä mahdollisimman paljon seuraavaa siirtoa laskettaessa. Lisätty "tappajasiirrot", joita säilytetään kaksi muistissa jokaiselle syvyydelle ja ne kokeillaan principal variationin jälkeen. Korjattu hashausongelma, joka aiheutti vääriä tasapelejä.
21.08.2016 | 4h | Suorituskykytestauksen aloitus, toteutusdokumentaation aloitus.
22.08.2016 | 1h | Tappajasiirrot ovat nyt varmasti eri siirtoja
23.08.2016 | 1h | Refaktoroitu luokkaa AILogic
24.08.2016 | 8h | Lisätty käliin puolen valinta AIta vastaan, AI:n vaikeustason valinta AivsAi tai pelaajaa vastaan ja käsitellään nyt valloitukset ennen muita siirtoja.
25.08.2016 | 5h | Sotilaiden korotus kumotaan nyt siirtoja testatessa, AILogic luokan metodeille lisää testejä ja metodit siis muutettu julkisiksi. Yritetty ratkaista pelin kaatuminen Play Again-valinna jälkeen.
26.08.2016 | 5h | Toteutusdokumentaatio tehty, suorityskykytestausta eteenpäin, lisää testejä AILogicille ja Play Again-bugin metsästystä.

