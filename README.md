<img src="https://raw.githubusercontent.com/blueseerERP/blueseer/blob/master/.github/src/images/basket.png">
<img class="logo" width="100px" height="100px" src="https://www.blueseer.com/img/bs.png" alt="Free ERP">
<h3>Developer: Terry Vaughn</h3>
<h3>latest release version: 4.3</h3>
<h3>latest release date: 2020-09-06</h3>
<h3>programming language: Java programming language</h3> 
<h3>operating system: Cross-Platform</h3>
<h3>genre:  Enterprise Resource Planning (ERP), Accounting, Personal Finance</h3> 
<h3>license: MIT License</h3>
<h3>website: www.blueseer.com</h3>

'''BlueSeer ERP''' is a Free open source ERP software package.  It was designed to meet the needs of
the manufacturing community for an ERP system that is easily customizable and
extendable while providing generic functionality that is typically observed in
most manufacturing environments.   
BlueSeer is released for free use under the MIT License.   The application and source code
are available for download at github.com.  The development of BlueSeer
began in 2005, and the latest 'stable' release of version 4.3 was released on Sept 9th 2020.</br>

<h1>Functionality</h1>

BlueSeer provides modules for the following generic set of business concepts : 
* Double Entry General Ledger
* Cost Accounting
* Accounts Receivable Processing and Aging
* Accounts Payable Processing and Aging
* PayRoll
* Inventory Control
* Job Tracking
* Lot Traceability
* Order Management
* Service Order and Quoting Management
* Electronic Data Interchange (EDI)
* UCC Label Generation
* Materials Resource Planning (MRP)
* Human Resources (HR)

<h2>Technology</h2>
BlueSeer ERP is written entirely in Java.  The application is a non-web based
desktop application that relies heavily on the Java Swing widget
toolkit/library.  There are currently two database engines available for
BlueSeer. 
For single client deployment, The relational database SQLite is used for
it's deployment ease and server-less design.  For multi-client
deployment scenarios, the open-source relational database MySQL is used as the
back-end database server.  MySQL was chosen for it's popularity and excellent
performance
reviews.  
</br>
BlueSeer is a menu-driven application.  It's "molecular composition" is a collection of Java Swing
JPanel widgets.  Each business function, i.e. Order Entry, Item Master
Maintenance, etc is a stand-alone JPanel widget.  Each JPanel widget is loaded
at runtime using Reflection to 'inject' the JPanel
into the JFrame on user
demand.  JPanel class names are stored in the database and associated
with  menu options which are further associated with user permissions.  This
archtitecture increases the capability of customization and extension by
engaging BlueSeer as
a Desktop Application Framework.  Applications independent of the core
software can be quickly deployed 
given the menu/class management and
permissions functionality that's built into the BlueSeer framework.
</br>
Current development interests are the inclusion of other open
source database packages for the back-end server.

