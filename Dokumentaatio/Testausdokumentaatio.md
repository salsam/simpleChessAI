#Testausdokumentaatio

###Toiminnan manuaalinen testaus

olen testannut manuaalisesti ohjelmannan toimintaa kaikissa kolmessa moodissa (pelaaja vs pelaaja, pelaaja vs tekoäly ja tekoäly vs tekoäly) katsomalla siirtojen toimivan oikein. Erityistapauksina olen testannut, ettei laudan tilanne muutu testattaessa shakkausta, matitusta tai tekoälyn seuraavaa siirtoa. Lisäksi toisena erityistapauksena sotilaan korotus kuningattareksi toimii oikein, eikä edellämainituissa erityistapauksissakaan aihauta ongelmia.

Tekoälyn toiminnan oikeellisuutta olen testannut myös manuaalisesti ja tekoälyn siirrot näyttävät yleensä toimivan odotettavasti eli sekunnin aikarajalla tekoäly pääsee rekursiotasolle 3-5 eikä näin tee shakkia taitamattoman mielestä tyhmiä siirtoja. Välillä loppupelissä tekoälyn heikkous kuitenkin näkyy, sillä käytän työssä runsaasti dynaamista muistia, jolloin pelin loppupuolella dynaamiseen muistiin kertyneet tiedot alkavat hidastaa tekoälyä. Näin laittamani debugviesti, joka ilmoittaa saavutetun rekursiosyvyyden tippuu aluksi 3-5:stä 2-3:een ja lopulta 1-2 aivan pelin loppupuolella.

Nämä testit voi toistaa ajamalla ohjelman, valitsemalla halutun pelimoodin ja pelaamalla peliä eteenpäin. Kuningattaren korotuksen testaus näin voi usein vaatia muutamia pelejä, joten suosittelen antamaan tekoälylle hyvin vähän miettimisaikaa. Itse käytin miettimisaikaa 100ms, jolloin en juurikaan joudu odottamaan tekoälyn siirtoa.

###Suorituskykytestaus

TBA
