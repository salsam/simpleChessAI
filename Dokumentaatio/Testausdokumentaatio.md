#Testausdokumentaatio

###Toiminnan manuaalinen testaus

olen testannut manuaalisesti ohjelmannan toimintaa kaikissa kolmessa moodissa (pelaaja vs pelaaja, pelaaja vs tekoäly ja tekoäly vs tekoäly) katsomalla siirtojen toimivan oikein. Erityistapauksina olen testannut, ettei laudan tilanne muutu testattaessa shakkausta, matitusta tai tekoälyn seuraavaa siirtoa. Lisäksi toisena erityistapauksena sotilaan korotus kuningattareksi toimii oikein, eikä edellämainituissa erityistapauksissakaan aihauta ongelmia.

Tekoälyn toiminnan oikeellisuutta olen testannut myös manuaalisesti ja tekoälyn siirrot näyttävät yleensä toimivan odotettavasti eli sekunnin aikarajalla tekoäly pääsee rekursiotasolle 3-5 eikä näin tee shakkia taitamattoman mielestä tyhmiä siirtoja. Välillä loppupelissä tekoälyn heikkous kuitenkin näkyy, sillä käytän työssä runsaasti dynaamista muistia, jolloin pelin loppupuolella dynaamiseen muistiin kertyneet tiedot alkavat hidastaa tekoälyä. Näin laittamani debugviesti, joka ilmoittaa saavutetun rekursiosyvyyden tippuu aluksi 3-5:stä 2-3:een ja lopulta 1-2 aivan pelin loppupuolella.

Nämä testit voi toistaa ajamalla ohjelman, valitsemalla halutun pelimoodin ja pelaamalla peliä eteenpäin. Kuningattaren korotuksen testaus näin voi usein vaatia muutamia pelejä, joten suosittelen antamaan tekoälylle hyvin vähän miettimisaikaa. Itse käytin miettimisaikaa 100ms, jolloin en juurikaan joudu odottamaan tekoälyn siirtoa.

###Suorituskykytestaus

###Esimerkkipeli, jossa rekursiosyvyydeksi rajoitettu 3 ja aikaraja 1s.

![Siirtoajan kehittyminen vuoronumeron kasvaessa](https://github.com/salsam/simpleChessAI/blob/master/Dokumentaatio/Siirtoon%20kulunut%20aika.jpg)

Kuvasta nähdään hyvin, miten aivan ensimmäisiin siirtoihin kuluu kaikkein eniten aikaa, sillä dynaamisessa muistissa ei vielä ole juuri tietoa. Valkoinen käyttää ensimmäisillä vuoroilla paljon mustaa enemmän aikaa, sillä valkoisen liikkuessa ensin, pystyy valkoinen hallitsemaan pelin alkua. Lisäksi evaluaatiofunktioni antaa myös siirtojen määrälle arvon, joten valkoinen ja musta pyrkivät lisäämään omia mahdollisuuksiaan ja vähentämään vastustajan. Näin tämä vaikutus vielä korostuu.


Pelin kehittyessä aikavaatimus laskee mahdollisten siirtojen määrän laskiessa, mutta dynaaminen muisti vielä korostaa tätä. Pelin aluksi tapahtuneet jyrkät pudotukset vaatimuksessa johtuvat siitä, että muta pyrki shakkaamaan valkoista varsin nopeasti ja näin karsimaan valkoisen mahdollisia siirtoja. Musta myös onnistui lyömään valkoisen kuningattaren nopeasti, mikä korosti tässä pelissä tätä entisestään. Näin mustan aikavaatimus saavuttaa ja ohittaa valkoisen mustan siirtyessä hallintaan.

Pelin puolivälissä havaitaan jyrkkä piikki, jossa 1s aikaraja rikkoutui, sillä pelitilanne oli todella monimutkainen sekä rekursio on mitä ilmeisimmin juuri saavuttanut uuden rekursiosyvyyden. Tämä piikki on mielestäni silti liian jyrkkä ollakseen vain tämän seurausta, joten veikkaisin transpositiotaulun rajoittamattoman koo'on olevan tässä osatekijänä.

Pelin loppua kohti tekoälyjen välinen aikavaatimusero laskee nollaan, kunnes peli päättyykin tasapeliin. Pelin lopussa rekursion matalasyvyys tulee esille, sillä valkoinen on kykenemätön matittamaan mustan, vaikka valkoisella on vuorolla 40 täysi hallinta pelissä. Kaikki mitä valkoisen olisi pitänyt tehdä oli sotilaiden saattaminen turvallisesti läpi. Tähän valkoisen olisi kuitenkin pitänyt hyödyntää kuningastaan, mutta evaluaatiofunktio ei tätä tajua, sillä kuningas sijaitsee nyt evaluaatiofunktion perusteella parhaassa ruudussa.

Kokonaisuudessaan valkoinen käytti siirtoihinsa keskimäärin 256 ms ja musta 291 ms. Tämä kuulostaa järkevältä, sillä musta onnistui kaappaamaan hallinnan valkoiselta jo varsin alussa ja hallitsi peliä pitkään noin vuorolle 40. Lisäksi mustalla on myös yksi hammusiirto, joka liene aiheutunut transpositiotaulun eksponentiaalisesta muistivaatimuksesta.

###Vuoroihin kuluneen ajan keskiarvon kehitys yli 10 peli, rekursiosyvyys 3, aikaraja 1s

