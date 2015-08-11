/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.cirilo;

public class CiriloResources_de extends java.util.ListResourceBundle {

      public Object[][] getContents() { return contents; }
 
      static final Object[][] contents = 
             {
    	  /* gui*/
                { "user"        , "Benutzer(Innen)name"         },
                { "passwd"      , "Kennwort"        },
                { "login"       , "Anmelden"          },
                { "cancel"      , "Abbrechen"       },
                { "pool"         , "Datenpool"         },
                { "yes"      , "Ja"       },
                { "no"         , "Nein"         },
                { "save"         , "Speichern"         },
                { "saveas"         , "Speichern unter"         },
                { "saveds"         , "Datenstrom speichern"         },
                { "errsave"         , "Fehler beim Speichern von Datei" },
                { "saveok"         ,  "Datenstrom {0} aus Objekt {1} konnte erfolgreich in Datei {2} gespeichert werden." },
                { "submit"         , "Übermitteln"         },
                { "edit"         , "Bearbeiten"         },
                { "close"         , "Schließen"       },
                { "open"          , "Öffnen"          },
                { "replace"          , "Ersetzen"          },
                { "export"          , "Exportieren"          },
                { "delete"          , "Löschen"          },
                { "new"          , "Neu"          },
                { "search"          , "Suchen"          },
                { "updatereg"          , "Register aktualisieren"          },
                { "geo"          , "Aggregieren"          },
                { "checkoai"          , "Für OAI-Harvester freigegeben"          },
                { "active"          , "Aktiv"          },
                { "inactive"          , "Inaktiv"          },
                { "ingestsim"          , "Ingestvorgang nur simulieren"          },
                { "ingestdir"          , "Aus Dateisystem"          },
                { "ingestex"          , "Aus eXist"          },
                { "ingestexcel"          , "Aus Excel"          },
                { "validate"          , "Validieren"          },
                { "showlog"          , "Protokoll anzeigen"          },
                { "reset"          , "Rücksetzen"          },
                { "apply"          , "Übernehmen"          },
                { "create"          , "Erstellen"          },
                { "add"          , "Hinzufügen"          },
                { "prop"          , "Eigenschaften"          },
                { "streams"          , "Inhaltsdatenströme"          },
                { "sysdata"          , "Systemdatenströme"          },
                { "rels"          , "Relationen"          },
                { "appear"          , "Wird dargestellt in:"          },
                { "sysprop"          , "Systemeigenschaften"          },
                { "dcfromtei"          , "Objekt aus Quelle aktualisieren"          },
                { "transfs"          , "Transformationen"          },
                { "streamid"          , "Datenstrom-ID"          },
                { "file"          , "Datei"          },
                { "transform"          , "Transformieren"          },
                { "simulate"          , "Simulieren"          },
                { "unmod"          , "-"          },
                { "sourcedir"          , "Quellverzeichnis"          },
                { "targetdir"          , "Zielverzeichnis"          },
                { "noconn", "Es konnte keine Verbindung zum Repository hergestellt werden."},
                { "server", "Fedora-Server"},
                { "imageserver", "Bildserver"},
                { "context", "Kontext"},
                { "protocol", "Protokoll"},
                { "url", "URL"},
                { "home", "Heimverzeichnis"},
                { "maketemplate", "Als Template hinzufügen"},
                { "extras.createenvironment", "Benutzerumgebung erstellen ..."},
                { "english", "Englisch"},
                { "german", "Deutsch"},
                { "language", "Sprache"},
                { "harvest", "Sammeln"},
                { "general", "Allgemeines"},
                { "teiupload", "Upload von TEI-Dokumenten"},
                { "lidoupload", "Upload von LIDO-Dokumenten"},
                { "metsupload", "Upload von DFG Viewer-Dokumenten"},
                { "defaultcm", "Default Content Model"},
                { "preferences", "Einstellungen"},
                { "texteditor", "Texteditor"},
                { "login", "Login"},
                { "loginiips", "Bildserver Login"},
               
                { "dcmapping", "Dublin Core-Metadaten extrahieren"},
                { "semextraction", "Regelwerk zur Extraktion semantischer Konstrukte anwenden"},
                { "removeempties", "Leere Elemente ohne Attribute entfernen"},
                { "createcontexts", "Context-Objekte erstellen"},
                { "resolveregex", "Reguläre Ausdrücke auflösen"},
                { "resolvegeoids", "PlaceName-Elemente gegen geonames.org auflösen"},
                { "ingestimages", "Bilder ingestieren"},
                { "resolveskos", "SKOS-Konzepte auflösen"},
                { "refreshsource", "Quelldokument mit expandiertem Inhalt überschreiben"},
                { "customization", "TEI-Customization ausführen"},
                { "createfromjpeg", "METS-Objekte aus Bildverzeichnissen erstellen"},
                { "geonameslogin", "Geonames Webservice Login-Name"},
                { "onlygeonameids", "Nur Elemente mit geonameID berücksichtigen"},
                                                   
                /* dialog */
                { "import"          , "Importieren"          },
                { "noedit"          , "Für diesen Inhaltstyp ist derzeit kein Bearbeitungsmodul verfügbar."          },
                { "choosefile"          , "Datei wählen"          },
                { "choose"          , "Auswählen"          },
                { "errimport"          , "Es ist ein Fehler beim Importieren von {0} aufgetreten. Die Datei konnte nicht korrekt validiert werden."          },
                { "show"          , "Anzeigen"          },
                { "update"      , "Datenstrom {0} aus Objekt {1} wurde erfolgreich mit dem Inhalt von Datei {2} aktualisiert."   },                             
                { "objmodsuc"          , "{0} Objekt(e) konnten erfolgreich verändert werden."          },
                { "chooseedir"          , "Exportverzeichnis wählen"          },             
                { "objexsuc"          , "{0} Objekt(e) konnten erfolgreich exportiert werden."          },
                { "addcont"          , "Sammeln läuft..."          },
                { "geosuc"          , "In {0} Objekt(en) konnten Geodaten erfolgreich aktualisiert werden."          },
                { "regsuc"          , "In {0} Objekt(en) konnten Register erfolgreich aktualisiert werden."          },
                { "refresh"          , "Aktualisieren"          },
                { "replaceobjc"          , "Objektinhalte ersetzen"          },
                { "objmod"          , "Dieser Vorgang verändert {0} Objekt(e). Sind Sie wirklich sicher, dass Sie diesen Vorgang durchführen möchten?"          },
                { "objdel"          , "Dieser Vorgang entfernt {0} Objekt(e) aus dem Repository. Sind Sie wirklich sicher, dass Sie diesen Vorgang durchführen möchten?" },
                { "objdelsuc"          , "{0} Objekt(e) aus dem Repository gelöscht."          },
                { "objex"          , "Dieser Vorgang exportiert {0} Objekt(e) aus dem Repository. Sind Sie wirklich sicher, dass Sie diesen Vorgang durchführen möchten?" },
                { "file.export"          , "Objekte exportieren"          },
                { "refreshcont"          , "Aktualisieren läuft..."          },
                { "chooseidir"          , "Ingestverzeichnis wählen"          },
                { "chooseimdir"          , "Importverzeichnis wählen"          },
                { "objcrea"          , "Dieser Vorgang erzeugt {0} Objekt(e) basierend auf Content Model {1} aus Datenquelle {2} im Repository. Möchten Sie wirklich fortsetzen?" },
                { "objimp"          , "Dieser Vorgang erzeugt {0} Objekt(e) aus Datenquelle {1} im Repository. Möchten Sie wirklich fortsetzen?" },
                { "askharv"          , "Dieser Vorgang sammelt Metadaten von {0} Providern. Sind Sie wirklich sicher, dass Sie diesen Vorgang durchführen möchten?" },
                { "ingestcont"          , "Ingestieren läuft..."          },
                { "harvcont"          , "Harvesten läuft..."          },
                { "start"          , " Beginn"          },
                { "end"          , " Ende"          },
                { "ofsim"          , " der Simulation"          },
                { "ofingest"          , " des Ingestvorganges: "          },
                { "ofimport"          , " des Importvorganges: "          },
                { "novalidtei"          ,"\n{0}. Datei {1} enthält kein valides TEI-Dokument. "},
                { "novalidrtei"          ,"\n{0}. Template produziert kein valides TEI-Dokument für Record {1}. "},
                { "objingr"          , "\n{0}. Objekt {1} wurde basierend auf Record {2} erstellt. "   },
                { "objingrrefr"          , "\n{0}. Objekt {1} wurde basierend auf Record {2} aktualisiert. "   },
                { "objing"          , "\n{0}. Objekt {1} wurde basierend auf Datei {2} erstellt. "   },
                { "objingrefr"          , "\n{0}. Objekt {1} wurde basierend auf Datei {2} aktualisiert. "   },
                { "denied"          , "\n{0}. Zugriff von User {1} auf Objekt {2} wurde zurückgewiesen. "   },
                { "novalidrmets"          ,"\n{0}. Template produziert kein valides METS-Dokument für Record {1}. "},
                { "novalidmets"          ,"\n{0}. Datei {1} enthält kein valides METS-Dokument. "},
                { "ingested"          , " Objekt(e) wurden ingestiert, "          },
                { "imported"          , " Objekt(e) wurden importiert, "          },
                { "existed"          , " Objekt(e) existieren bereits und "          },
                { "failed"          ," Datei(en) enthalten kein valides FEDORA Objekt."},
                { "harvested"          , "Metadaten-Harvesting wurde beendet. "          },
                { "refreshed"          , " aktualisiert. "          },
                { "details"          , "Näheres zum Vorgang entnehmen Sie bitte dem Ereignisprotokoll unter "          },
                { "invalauthor"          , "Ungültige Authorisierung!"          },
                { "invalauthent"          , "Ungültige Authentifizierung!"          },
                { "double"          , "Ein Datenobjekt mit dem Persistent Identifier {0} existiert bereits."          },
                { "objowner"          , "Ein Datenobjekt mit dem Inhaltsmodell {0} und EigentümerInnenrechten für {1} wurde unter dem Persistent Identifier {2} erstellt."          },
                { "errcrea"          , "Fehler beim Erstellen eines neuen Datenobjektes mit dem Inhaltsmodell {0}."          },
                { "nopid"          , "Es wurde kein Persistent Identifier angegeben."          },
                { "choosesdir"          , "Quellverzeichnis wählen"          },   
                { "choosetdir"          , "Zielverzeichnis wählen"          }, 
                { "choosedirstyle"          , "Wählen Sie bitte ein Quell- bzw. Zielverzeichnis und ein XSLT-Stylesheet aus."          }, 
                { "object"          , "Objekt"          },
                { "oftrans"          , " der Transformation"          },
                { "transcont"          , "Transformieren läuft ..."          },
                { "objmodf3"          , "Dieser Vorgang verändert {0} Objekte ({1} in {2}.*) im Repository nachhaltig. Möchten Sie wirklich fortsetzen?"          },
                { "objmode"          , "Dieser Vorgang erzeugt {0} Dateien in Verzeichnis {1}. Möchten Sie wirklich fortsetzen?"          },
                { "log"          , "{0} Dateien wurden erstellt, bei {1} Dateien trat ein Fehler auf."          },
                { "addrel"          , "Ergänzen des Systemdatenstroms RELS-EXT läuft ..."         },
                { "relsuc"          , "{0} Objekt(e) wurde(n) um den Systemdatenstrom RELS-EXT erweitert."         },
                { "envok"          , "Systemumgebung für BenutzerIn {0} wurde erstellt"         },
                { "choosetemp"          , "Templatedatei wählen"         },
                { "choosesource"          , "Datenquelle wählen"         },
                { "ingest"          , "Ingestieren"         },
                { "import"          , "Importieren"         },
                { "evaluate"          , "Evaluieren"         },
                { "showtrip"          , "Triples anzeigen"         },
                { "results"          , "Ergebnisse"         },
                { "exceltable"          , "Excel-Tabelle"         },
                { "teitemp"          , "Template"         },
                { "provider",            "Provider" },
                { "baseurl",            "Base URL" },
                { "prefix",            "Metadatenpräfix" },
                { "updated",            "Letztes Update" },
                { "relsintsuc",            "Der Datenstrom RELS-INT des Objekts {0} wurde erfolgreich aktualisiert." },
                { "valerror",            "Validierungsfehler" },
                { "creatingobject",            " Objekt wird erstellt " },
                { "objectnotfound",            "Objekt {0} konnte nicht gefunden werden." },
                { "imagenotfound",            "Bilddatei {0} für Objekt {1} konnte nicht gefunden werden." },
                { "updatingobject",            " Objekt wird aktualisiert " },
                { "excelformat",            " Datei {0} hat ein nicht unterstütztes Format. Bitte benutzen Sie [Excel OOXML]." },                                
                { "xmlformat", 		"Fehlerhaftes XML-Dokument. Datenstrom konnte nicht gespeichert werden." },
                { "selectformat", 		"Cirilo kann derzeit FEDORA-Objektdateien mit folgenden Formaten importieren:" },
                { "ingfail", 		"Ingest ist fehlgeschlagen" },
                { "alrexist", 		"Objekt existiert bereits" },
                { "attingest",        "Importieren von Datei {0} mit Format {1} und PID {2}"},         
                { "novalidobj",       "Datei {0} enthält kein valides FEDORA-Objekt."},
                { "ingestof",       "Ingest von Datei "},
                { "tei2mets",       "METS Datenstrom erzeugen"},
              
                /* cirilo */
                { "loadprop"          , "Einstellungen lesen"          },
                { "sam"          , "Berechtigungsmanager initialisieren"          },
                { "dam"          , "Dialogmanager initialisieren"          },
                { "notitle"          , "Ohne Titel"          },
                
                /* menu */
                { "file.edit"          , "Objekte bearbeiten"          },
                { "file.ingest"          , "Objekte ingestieren"          },
                { "file.import"          , "Objekte importieren"          },
                { "Extras"          , "Extras"          },
                { "file.transform"          , "Dateien transformieren"          },
                { "resetdeskt"          , "Desktop zurücksetzen"          },
                { "exit"          , "Beenden"          },
                { "changeuser"          , "Repository wechseln"          },
                { "Infos"          , "Infos"          },
                { "about"          , "Über Cirilo"          },
                { "File"          , "Datei"          },           
          
                /* setup */
                { "createobj"          , "Objekt erstellen"          },
                { "editdc"          , "Dublin Core bearbeiten"          },
                { "editobjsing"          , "Objekt bearbeiten"          },
                { "choosestyle"          , "XSL-Stylesheet auswählen"          },
                { "extras.harvest"          , "Metadaten sammeln"          },
                { "extras.templater"          , "RDF Template Tester"          },
                { "extras.preferences"          , "Einstellungen ..."          },
                { "ingestexcel"          , "Aus Exceltabelle"          },
                { "existlogin"          , "Login eXist-Datenbank"          },
                                
                /* table header */
                { "pid"          , "PID"          },
                { "title"          , "Titel"          },
                { "contentmodel"          , "Inhaltsmodell"          },
                { "lastupdate"          , "Letzte Änderung"          },
                { "owner"          , "Erstellt von"          },
                
                { "id"          , "Datenstrom ID"          },
                { "label"          , "Label"          },
                { "versionable"          , "Versionierbar"          },
                { "mimetype"          , "Mime Type"          },
                { "replaceversion"          , "Update ersetzt die letzte aktuelle Version"          },
                { "createversion"          , "Update erstellt neue Version"          },
                { "createstream"          , "Datenstrom erstellen"          },
                { "nonvalidid",            "Fehler beim Erstellen von Datenstrom {0}. Das ist ein reservierter Identifikator." },
                { "nonvaliddel",            "Fehler beim Löschen von Datenstrom {0}. Defaultdatenströme können nicht gelöscht werden." },
                { "delstream",            "Dieser Vorgang entfernt {0} Datenströme. Sind Sie wirklich sicher, dass Sie diesen Vorgang durchführen möchten?" },
                { "userdef"          , "Benutzerdefiniertes Stylesheet"          },
                { "nonsysadm"          , "Das Löschen von Systemobjekten ist nur AdministratorInnen erlaubt. Objekt {0} konnte nicht gelöscht werden."          },
                { "parsererror"          , "Beim XML Parsing von Datei {0} ist ein Fehler aufgetreten."          },

                { "datalocations"          , "Datenstromreferenzen"          },
                
                { "hdlprefix"          , "Handle-Prefix:"          },
                { "proprefix"          , "Projekt-Prefix:"          },
                { "numcons"          , "Fortlaufend numerieren:"          },
                { "startw"          , " Beginn bei: "          },
                { "getkey"          , "Key holen"          },
                { "hdlmanage"          , "Handles verwalten"          },
                { "hdlcreate"          , "Handles erstellen ..."          },
                { "hdldel"          , "Handles löschen ..."          },
                { "hdlauthfailed"          , "Authentifizierung am Handle Service ist fehlgeschlagen! Es konnte keine gültige Schlüsseldatei gefunden werden."          },
                { "hdlsel"          , "Schlüsseldatei wählen"          },
                { "hdlvalid"        ,  "Authentifizierung am Handle Service war erfolgreich. Handles können nun bearbeitet werden."}, 
                { "hdlcreated"  , " Für {0} Objekt(e) wurden Handle(s) erstellt."          },
                { "hdldeleted"  , "Die Handle(s) von {0} Objekt(en) wurden entfernt."          },
                { "shownat"  , "Shown At"          },
                { "cmodel"  , "Inhaltsmodell"          },

             };    


}
