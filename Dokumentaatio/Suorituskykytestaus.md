#Suorituskykytestaus

Tutkin aikavaativuutta laittamalla kaksi tekoälyä pelaamaan vastakkain ja kirjaamalla ylös keskimääräisen aikavaativuuden ensimmäisen 50 siirron ajan (tai mainitsen erikseen jos peli loppui ennen tätä) kolmella toistolla mainituilla ehdoilla. Syy ainoastaan kolmen sarjan käytölle on yksinkertaisesti testiin kuluva aika. Kirjaan ylös myös välituloksina keskimääräisen aikavaativuuden tähän mennessä aina 10 siirron välein. Testi luottaa oletukseen siitä, että tekoäly tekee lähes samat siirrot joka kerta, jolloin aineisto on hyvin vertailukelpoista. Käytän testauksessa alle 300 euron läppäriä, joten saamani ajat tulevat olemaan jopa 10-kertaiset tehokkaaseen pöytäkoneeseen nähden. 

###Rekursiosyvyys 3, 3 'tappajasirtoa' ja liikkuvuuden painotus 10
Tappajasiirrot ovat siis siirtoja, jotka aiheuttivat puussa aiemmin beta-katkaisun vähentäen tutkittavien solmujen määrää. Säilytän näitä siis maksimissaan kolme ja korvaan aina vanhimman uudella, jos löydetään uusi tappajasiirto. Rekursiosyvyys 3 eli algoritmini käy tilannetta 3 siirtoa eteenpäin. Tarkoituksenani on selvittää, kannattaako minun säilyttää kolmea vai kahta siirtoa. Tuloksena saatiin taulukko (ajat millisekunteina):

Siirtoja\Suorituskerta | 1 | 2 | 3 | Keskiarvo
------ | ----- | ----- | ----- | ------
10 | 3793 | 3734 | 3672 | 3733
20 | 4514 | 4465 | 4471 | 4483
30 | 5395 | 5435 | 5606 | 5478
40 | 6122 | 6156 | 6345 | 6207
50 | 5855 | 6209 | 6072 | 6045

Kuten taulukossa näkyykin hyvin, aluksi nappulat ovat kasassa, eikä mahdollisia siirtoja ole useaa. Näin aikavaativuus pienin aluksi, mutta pelin edetessä ensin mahdollisuuksien määrä kasvaa, kunnes alkaa supeta nappuloiden tullessa lyödyksi ja sotilaiden tukkiessa lautaa.

###Rekursiosyvyys 3, 2 'tappajasiirtoa' ja liikkuvuuden painotus 10
Tämä toimii vertailukohtana edelliselle ja tämän testin tulos ratkaisee sen, kuinka useaa tappajasiirtoa säilytän muistissa. 

Siirtoja\Suorituskerta | 1 | 2 | 3 | Keskiarvo
------ | ----- | ----- | ----- | ------
10 | 3945 | 3695 | 3688 | 3776
20 | 4915 | 4895 | 4683 | 4831
30 | 5804 | 5620 | 5442 | 5622
40 | 6860 | 6476 | 6324 | 6553
50 | 6675 | 6675 | 6057 | 6469

Jälleen havaitaan sama kehitys keskimäääräisessä aikavaativuudessa, jossa aluksi vaatimus kasvaa ja lopuksi laskee. Verrattuna 3 tappajasiirron käyttöön liikkuvuuden ollessa pieni, ei eroa juuri esiinny, mutta mahdollisuuksien lisääntyessä alkaa kolmen tappajasiirron etu näkyä. Näin tulenkin jatkossa käyttämään kolmea tappajasiirtoa.

###Rekursiosyvyys 2, 3 tappajasiirtoa ja liikkuvuuden painotus 10
Siirtoja\Suorituskerta | 1 | 2 | 3 | Keskiarvo
------ | ----- | ----- | ----- | ------
10 | 268 | 386 | 318 | 324
20 | 251 | 304 | 233 | 263
30 | 266 | 265 | 202 | 244
40 | 227 | 227 | 175 | 210
50 | 224 | 192 | 163 | 193

Lisähuomautus: 3. kerta loppui tasapeliin vuorolla 45 tekoälyn toistaessa saman tilanteen kolmannen kerran (tämä johtunee bugista, joka aiheuttaa sen, ettei tekoäly tajua tilanteen toistuneen jo kolme kertaa). Tällä kertaa emme huomaa aikavaativuuden kasvua aluksi, vaan aikavaativuus laskee koko matkan alusta loppuun. Tämä johtunee siitä, että ensimmäisen sirron jälkeen tekoälyni hyödyntää dynaamista muistia kokeillen edellisen siirron laskuissa havaitut parhaat siirrot ensin. Näin ensimmäinen siirto on luonnostaan muita raskaampi ja rekursiosyvyyden ollessa pieni, ovat muut siirrot niin nopeita laskea, että tämä painottuu suuresti. Siirtojen nopeus johtunee siitä, että liikkuvuus ei ehdi vaikuttaa läheskään samalla tavoin rekursiosyvyydellä 2 kuin 3, kuten eksponentiaalinen aikavaativuus 160^d aavistikin. 

Toisaalta aikavaativuuden pienuuteen vaikuttaa myös se, että molemmat teoälyt tekevät huomattavasti tyhmempiä siirtoja, kuin rekursiosyvyyttä 3 käytettäessä. Näin pelaajat uhraavat jatkuvasti nappuloita ilman syytä, jolloin liikkuvuus vähenee nopeasti. Hassua kyllä, pelaajat myös shakkaavat vastustajan kuningasta koko ajan, jolloin vain pieni osa siirroista on laillisia ja liikkuvuus pienenee entisestään. Paras keksimäni selitys jatkuvalle shakkaukselle on vastustajan siirtojen rajoittaminen tajuamatta omien nappuloiden uhraamista prosessin aikana.


###Rekursiosyvyys 3, 3 ts, liikkuvuuden paino 10 ja korjattu alfa-beeta. 2 tietokonetta vastakkain 3s aikarajalle, maksimirekursiosyvyys 3. Lisäksi valloitukset tutkitaan ennen muita siirtoja, toisin kuin aikaisemmissa testeissä.

Testissä käytetään korjattua alfa-beeta karsintaa, jossa olen muistanu lopettaa nappuloiden testaamisen, kun beeta-katkaisu tapahtuu toisin kuin edellisissä testeissä. Alla on kirjattu kahdesta eri pelistä valkoisen ja mustan tekoälyn siirtoihin käyttämän ajan keskiarvo (millisekunteina)  tiettyihin siirtojen määriin asti. Siirtoja tarkoittaa montako siirtoa kyseinen pelaaja on tehnyt eli yhden testin aikana pelissä tehdään yhteensä 60 siirtoa.Keskiarvot on pyöristetty lähimpään kokonaislukuun.

Siirtoja\Pelaaja | valkoinen1 | musta1 | valkoinen2 | musta2 | Keskiarvo(valkoinen) | Keskiarvo(musta) | Keskiarvo
------ | ----- | ----- | ----- | ------ | ------ | ------ | ----- | -----
5 | 932 | 695 | 1116 | 627 | 1024 | 661 | 843
10 | 1064 | 765 | 1095 | 723 | 1080 | 744 | 912
15 | 909 | 799 | 1090 | 724 | 1000 | 762 | 883
20 | 881 | 879 | 922 | 623 | 902 | 751 | 827
25 | 780 | 785 | 808 | 623 | 794 | 704 | 749
30 | 698 | 697 | 721 | 608 | 710 | 653 | 682

Tuloksista näemme jälleen kerran pelitilanteen aluksi monimutkaistuvan, mutta alkavan sitten yksinkertaistua npappuloiden vähetessä. Lisäksi huomaamme, että alfa-beeta karsinnassa tekemäni moka vaikutti aikavaativuuteen todella paljon, sillä aikavaativuus on enimmillään kahdeksasosa aikaisemmasta. Tämä moka siis selittää myös aiemmin kurssilla yhtäkkiä tapahtuneen hitanemisen, sillä olin luultavasti tuolloin poistanut "turhan" katkaisun. Hieman yllättäen huomaamme myös eron eri pelaajien välillä, pelin alussa valkoinen käyttää huomattavasti enemmän aikaa siirtojen laskemiseen kuin musta. Pelin loppupuolella tilanne kuitenkin tasaantuu. Oma veikkaukseni tämän syystä on se, että valkoisen siirtäessä ensin on valkoisella enemmän mahdollisia siirtoja kuin mustalla. Tämä heuristiikka perustuu siihen, että valkoisen tehdessä siirron, mustan mahdolliset siirrot todennäköisesti vähenevät valkoisen lyödessä nappuloita ja tukkiessa lautaa. Näin valkoisen saama kehitysetu antaa toisaalta valkoiselle edun mustaa vastaan, mutta myös hidastaa laskentaa mahdollisuuksien ollessa lukuisampia. Pelin vanhetessa vuoron kehitysetu kuitenkin laskee prosentuaalista osuuttaan ja pelitilanne tasoittuu.
