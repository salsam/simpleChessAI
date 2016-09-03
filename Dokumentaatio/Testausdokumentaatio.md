#Testausdokumentaatio

###Toiminnan manuaalinen testaus

olen testannut manuaalisesti ohjelmannan toimintaa kaikissa kolmessa moodissa (pelaaja vs pelaaja, pelaaja vs tekoäly ja tekoäly vs tekoäly) katsomalla siirtojen toimivan oikein. Erityistapauksina olen testannut, ettei laudan tilanne muutu testattaessa shakkausta, matitusta tai tekoälyn seuraavaa siirtoa. Lisäksi toisena erityistapauksena sotilaan korotus kuningattareksi toimii oikein, eikä edellämainituissa erityistapauksissakaan aihauta ongelmia.

Tekoälyn toiminnan oikeellisuutta olen testannut myös manuaalisesti ja tekoälyn siirrot näyttävät yleensä toimivan odotettavasti eli sekunnin aikarajalla tekoäly pääsee rekursiotasolle 3-5 eikä näin tee shakkia taitamattoman mielestä tyhmiä siirtoja. Välillä loppupelissä tekoälyn heikkous kuitenkin näkyy, sillä käytän työssä runsaasti dynaamista muistia, jolloin pelin loppupuolella dynaamiseen muistiin kertyneet tiedot alkavat hidastaa tekoälyä. Näin laittamani debugviesti, joka ilmoittaa saavutetun rekursiosyvyyden tippuu aluksi 3-5:stä 2-3:een ja lopulta 1-2 aivan pelin loppupuolella.

Nämä testit voi toistaa ajamalla ohjelman, valitsemalla halutun pelimoodin ja pelaamalla peliä eteenpäin. Kuningattaren korotuksen testaus näin voi usein vaatia muutamia pelejä, joten suosittelen antamaan tekoälylle hyvin vähän miettimisaikaa. Itse käytin miettimisaikaa 100ms, jolloin en juurikaan joudu odottamaan tekoälyn siirtoa.

##Suorituskykytestaus

###Esimerkkipeli, jossa rekursiosyvyydeksi rajoitettu 3 ja aikaraja 1s.

![Siirtoajan kehittyminen vuoronumeron kasvaessa](https://github.com/salsam/simpleChessAI/blob/master/Dokumentaatio/Siirtoon%20kulunut%20aika.jpg)

Kuvasta nähdään hyvin, miten aivan ensimmäisiin siirtoihin kuluu kaikkein eniten aikaa, sillä dynaamisessa muistissa ei vielä ole juuri tietoa. Valkoinen käyttää ensimmäisillä vuoroilla paljon mustaa enemmän aikaa, sillä valkoisen liikkuessa ensin, pystyy valkoinen hallitsemaan pelin alkua. Lisäksi evaluaatiofunktioni antaa myös siirtojen määrälle arvon, joten valkoinen ja musta pyrkivät lisäämään omia mahdollisuuksiaan ja vähentämään vastustajan. Näin tämä vaikutus vielä korostuu.


Pelin kehittyessä aikavaatimus laskee mahdollisten siirtojen määrän laskiessa, mutta dynaaminen muisti vielä korostaa tätä. Pelin aluksi tapahtuneet jyrkät pudotukset vaatimuksessa johtuvat siitä, että muta pyrki shakkaamaan valkoista varsin nopeasti ja näin karsimaan valkoisen mahdollisia siirtoja. Musta myös onnistui lyömään valkoisen kuningattaren nopeasti, mikä korosti tässä pelissä tätä entisestään. Näin mustan aikavaatimus saavuttaa ja ohittaa valkoisen mustan siirtyessä hallintaan.

Pelin puolivälissä havaitaan jyrkkä piikki, jossa 1s aikaraja rikkoutui, sillä pelitilanne oli todella monimutkainen sekä rekursio on mitä ilmeisimmin juuri saavuttanut uuden rekursiosyvyyden. Tämä piikki on mielestäni silti liian jyrkkä ollakseen vain tämän seurausta, joten veikkaisin transpositiotaulun rajoittamattoman koo'on olevan tässä osatekijänä.

Pelin loppua kohti tekoälyjen välinen aikavaatimusero laskee nollaan, kunnes peli päättyykin tasapeliin. Pelin lopussa rekursion matalasyvyys tulee esille, sillä valkoinen on kykenemätön matittamaan mustan, vaikka valkoisella on vuorolla 40 täysi hallinta pelissä. Kaikki mitä valkoisen olisi pitänyt tehdä oli sotilaiden saattaminen turvallisesti läpi. Tähän valkoisen olisi kuitenkin pitänyt hyödyntää kuningastaan, mutta evaluaatiofunktio ei tätä tajua, sillä kuningas sijaitsee nyt evaluaatiofunktion perusteella parhaassa ruudussa.

Kokonaisuudessaan valkoinen käytti siirtoihinsa keskimäärin 256 ms ja musta 291 ms. Tämä kuulostaa järkevältä, sillä musta onnistui kaappaamaan hallinnan valkoiselta jo varsin alussa ja hallitsi peliä pitkään noin vuorolle 40. Lisäksi mustalla on myös yksi hammusiirto, joka liene aiheutunut transpositiotaulun eksponentiaalisesta muistivaatimuksesta.


Tämän testin voi toistaa muuttamalla AILogic luokan final int plies kenttään arvon 3 eli maksimirekursiosyvyydeksi 3 ja pelaamalla sitten normaalisti AI vs AI moodia kahdella tekoälyllä, joilla on oletus aikarajoitus.

###Siirtoihin kuluneen ajan keskiarvon kehitys keskiarvona 10 tekoälyjen välisestä pelistä, rekursiosyvyys 3, aikaraja 1s.

![Siirtoihin keskimäärin käytettyn ajan kehitys pelin aikana](https://github.com/salsam/simpleChessAI/blob/master/Dokumentaatio/Siirtoon%20k%C3%A4ytetty%20keskim%C3%A4%C3%A4r%C3%A4inen%20aika.jpg)

Lisäksi näistä 10 pelistä on mainittava, että neljä peliä loppui ennen vuoroa 50. Nämä pelit loppuivat kaikki vuoron 40 tienoilla ja näistä valkoinen voitti kaksi, musta voitti yhden ja yksi päättyi tasapeliin saman tilanteen toistuessa kolmannen kerran. 

Kuvaajasta näemme helposti alaspäin laskevan trendin eli siirtojen laskemiseen kuluu vähemmän aikaa laudalla olevien nappuloiden vähentyessä. Tähän toisaalta vaikuttaa myös dynaamiseen muistiin tallentuvat pelitilanteiden arvot. Hieman yllättäen valkoisen siirtoihin käyttämä aika on kuitenkin aina suurempi kuin mustan, eikä pelaajien välinen ero vähene juurikaan ajan kuluessa. Valkoinen ylipäätään hallitsi useimmiten peliä ja näytti siltä, että ensimmäiseksi siirtäminen antoi valkoiselle todella suuren eron. Käyttäessäni aikarajoitusta ilman rajoitusta rekursiosyvyydelle en kuitenkaan huomannut samanlaista ilmiötä. Periaatteessa tämä on loogista, sillä samalla rekursiosyvyydellä molemmilla pelaajilla on aina saman verran tietoa käytössä, jolloin valkoinen pystyy useimmiten säilyttämään pelin alussa luodun edun (. Toisaalta, kun laskentaa on rajoitettu vain ajalla, on häviöllä olevalla pelaajalla vähemmän mahdollisia siirtoja ja näin ylimmällä pelipuun tasolla on vähemmän solmuja. Ylimmän tason solmut toisaalta vaikuttavat kaikkein eniten haun aikavaativuuteen ja näin saman tasoisten tekoälyjen pelatessa vastakkain saa häviöllä oleva tekoäly "tasoitusta". Tätä en tullut aikaisemmin ajatelleeksi, kun ihmettelin miksi tekoälyjen peleillä on tapana venyä, jos valkoinen ei voita ennen vuoroa 50.

Tässä havaitaan melko suuria eroja aikaisempaan esimerkkipeliin, jota analysoin tarkemmin. Esimerkiksi edellisessä pelissä musta onnistui poikkeuksellisesti saamaan hallinnan ensimmäisen 40 vuoron aikana, mitä ei tapahtunut yhdessäkään pelissä materiaalisesti, vaikka yhdessä pelissä musta onnistui matittamaan valkoisen ovelalla kuningattaren siirtelyllä. Valitsemani esimerkkipeli ei siis tainnut osua aivan kohdalleen, mutta toisaalta se on hyvä esimerkki siitä, etteivät tekoälyni siirrot ole täysin ennalta kirjoitettuja edes samanlaista tekoälyä vastaan.

Tämän testin voi toistaa muuttamalla AILogic luokan final int plies kenttään arvon 3 eli maksimirekursiosyvyydeksi 3 ja pelaamalla sitten normaalisti AI vs AI moodia kahdella tekoälyllä, joilla on oletus aikarajoitus. Lisäksi tulee käynnistää ohjelma aina uudestaan jokaisen pelin jälkeen, jottei play again-valinta vaikuttaisi siirtojen aikavaativuuteen.

Tässä vielä linkki suorituskyvyn kehitykseen ohjelmaa rakennettaessa: [suorituskyvyn kehitys](https://github.com/salsam/simpleChessAI/blob/master/Dokumentaatio/Suorituskykytestaus.md).
