<img src="https://github.com/blueseerERP/blueseer/blob/master/src/images/bs65image.png" alt="Free ERP">
<!-- <img class="logo" width="100px" height="100px" src="https://www.blueseer.com/img/bs.png" alt="Free ERP"> -->
<a href="https://foojay.io/today/works-with-openjdk"><img align="right" src="https://github.com/foojayio/badges/raw/main/works_with_openjdk/Works-with-OpenJDK.png" width="100"></a>
<h3>Developer: Terry Vaughn</h3>
<h3>latest release version: 6.8</h3>
<h3>latest release date: 2024-04-04</h3>
<h3>programming language: Java programming language</h3> 
<h3>operating system: Cross-Platform</h3>
<h3>genre:  Enterprise Resource Planning (ERP), EDI, Accounting, Personal Finance</h3> 
<h3>languages supported: English, French, Spanish, Turkish, German, Romanian, Arabic, Chinese</h3>
<h3>license: MIT License</h3>
<h3>website: www.blueseer.com</h3>



<img src="https://github.com/blueseerERP/blueseer/blob/master/src/images/market2.png" alt="Free ERP image 2">
'''BlueSeer ERP''' is a Free open source multilingual ERP software package.  It was designed to meet the needs of
the manufacturing community for an ERP system that is easily customizable and
extendable while providing generic functionality that is typically observed in
most manufacturing environments.  BlueSeer also provides a fully functional EDI mapping tool for EDI translations and file traffic monitoring. 
BlueSeer is released for free use under the MIT License.   The application and source code
are available for download at github.com. BlueSeer was originally launched in 2017 and continues to evolve to meet user demands.
The latest 'stable' release of version 6.8 was released on 2024-04-04.</br>

<h1>Functionality</h1>

BlueSeer provides modules for the following generic set of business concepts : 
* Double Entry General Ledger
* Cost Accounting
* Accounts Receivable Processing and Aging
* Accounts Payable Processing and Aging
* PayRoll
* APIs for system to system integration
* Inventory Control
* Job Tracking
* Job / Operation Scanning
* Lot Traceability
* Purchasing
* Order Management
* Service Order and Quoting Management
* Freight Management
* Electronic Data Interchange (EDI)
* EDI Mapping tool (supports: X12, EDIFACT, CSV, FlatFile [IDOC, etc], XML, JSON )
* EDI Communications (FTP, AS2 server/client)
* Automated Task/Cron Scheduler
* UCC Label Generation
* Materials Resource Planning (MRP)
* Human Resources (HR)

<h1>Technology</h1>
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
BlueSeer is a menu-driven application.  It's composition is a collection of Java Swing
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

<h1>Build/Compile Instructions</h1>
</br>

<h2>Using Apache Netbeans</h2>

To use Netbeans, you will first need to download the Netbeans IDE (version 12 or higher). Once you have Netbeans installed, the following steps can be used to compile BlueSeer and bring up a test instance of the application :
1. Download the blueseer source from github. You can either 'git clone https://github.com/BlueSeerERP/blueseer.git' or download the zipped version of Blueseer from github.com/BlueseerERP and extract the contents into a directory called 'blueseer').
2. Open a command prompt and cd to the install directory 'blueseer/test'. This will be your working/testing directory
3. Type './refresh.bat' or ('./refresh.sh' for linux) to establish a test instance of the blueseer application along with the bs.cfg file and database instance
4. Start Netbeans and choose 'Open Project' to open the blueseer project files.
5. Right click on the blueseer project and go to Project Properties
6. Click on the 'run' portion of the properties and set the working directory to the 'test' directory where the instance config files and data directories are located.
7. You should now be able to build and run the application. The default login credentials are 'admin' and 'admin' respectively.
</br>

<h1>Contributing</h1>

Here's a simple guide to contribute to the BlueSeer project:
    
1. Fork the project
2. `git clone` your new fork to local client
3. Create a unique branch (`git checkout -b mybranch/mycode_change`)
4. Make your changes
5. Commit your changes (`git commit -m 'mycode with enhancement/fix/feature.'`)
6. Push to the branch (`git push origin mybranch/mycode_change`)
7. Open a pull request
</br>

<h1>License</h1>

MIT License (see [license.txt](LICENSE))


<h1>Logo and Trademark Policy</h1>

Please read our [Logo and Trademark Policy](TRADEMARK_POLICY.md).
