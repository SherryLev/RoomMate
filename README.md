# Team 101-13

## HouseMate
HouseMate is designed for university students and young working individuals that share their living space with others to aid in tackling common household issues such as keeping the house clean, paying rent, and keeping track of expenses by having an all-in-one chore designation and tracker, calendar to keep track of important dates, as well as an expense splitting function.

## Wiki
Access our wiki [here](https://git.uwaterloo.ca/gmmoroie/team-101-13/-/wikis/Project-Proposal:-HouseMate)

## Release Notes Version 1.3.0
**_What's New_**
- **Stats Screen UI:** We designed our statistics screen, enhancing our user experience https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/42
- **Expenses UI:** We designed our expenses screen and an add expenses function to enhance our user experience https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/20
- **Connected Expenses UI to Firestore:** We connected our expenses to the firestore, all of our users can store their expenses data on the cloud https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/39
- **Logout Feature:** User's can now log out of our app https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/15
- **Group Creation:** Groups can now be created with their unique group code and group information being stored in the Firestore https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Chore Creation:** Chores can now be created and stored in the Firestore https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/37
- **Chore Repeatability:** Chores can now be selected to be repeated weekly and monthly

**_In Progress (Expected in Next Release)_**
- **Joining Groups:** Implementing the ability for a user to join a group through a group code https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Group Code Displayed:** Implementing group codes to be displayed once a user creates a group https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Stats Screen Graphics:** Displaying user trends in chore completion and expenses 
- **Chore Ratings:** Implementing the ability for a user to both rate a chore and view the ratings on their chores
- **Home Screen:** Improving the UI for the current home screen 


**_Enhancements_**
- Created more efficient Firestore access calls for users, groups, chores and expenses
- App does not crash anymore when a user is logged in for too long

**_Download Installer_**
Download out installer for HouseMate here: https://git.uwaterloo.ca/gmmoroie/team-101-13/-/blob/main/housemate_sprint3.apk?ref_type=heads

For a full list of the changes visit our issues list on GitLab

## Release Notes Version 1.2.0
**_What's New_**
This is our largest update to Housemate yet! 
- **Group Visability:** Users can now see what their Household name is and what their group code is https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Switch Groups Functionality:** Users can now switch groups on the settings page as long as they provide a valid group code with messages on blank or incorrect codes and a success message upon a group switch https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Group and User Data Management:** Users are now added to the members list in the group collection in firestore and their group code is added to their user collection in firestore https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Group Creation Screen:** Users can now see what their Household name and group code is once they create a group upon registration https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Group Joining Screen:** Users can now see what their Household name and group code is once they join a group https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Household Personalization:** Users can now create a personalized name for their Household upon registration which is then shown on the HomePage https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/38
- **Chore Management:** Users can now delete chores and choose to repeat certain chores at their desired frequency https://git.uwaterloo.ca/gmmoroie/team-101-13/-/merge_requests/33
- **Chore Data Management:** Chores are now uploaded to the firestore upon creation with them being repeated over a four month term, they are also removed from the firestore upon deletion https://git.uwaterloo.ca/gmmoroie/team-101-13/-/merge_requests/33
- **Chore Visability:** A users upcoming chores are now shown on the HomeScreen https://git.uwaterloo.ca/gmmoroie/team-101-13/-/merge_requests/42
- **Username Selection:** Users can now choose a user name when they are registering their account https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/48
- **Expenses Data Management:** Expenses in the firestore are now connected to specific userId's given on who is involved in the payments
- **Expenses Summary:** Expenses summary is now connected to the firestore https://git.uwaterloo.ca/gmmoroie/team-101-13/-/merge_requests/36
- **Bug Fixes for Expenses:** We have fixed bugs in the expenses page, that is the error on splitting equally and the total adding to 1 cent more or less than the original payment https://git.uwaterloo.ca/gmmoroie/team-101-13/-/merge_requests/36
- **Settle Up UI:** We have created the settle up UI and functionality https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/44 , https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/43
- **Expense History:** Payments now show up in expense history and are updated in the firestore
- **Chore Ratings:** Chores can now be rated on a scale of 1-5 stars based on quality by other group mates https://git.uwaterloo.ca/gmmoroie/team-101-13/-/merge_requests/39
- **Expense Deletion:** Expenses can now be deleted once they are settled https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/47
- **Calendar Removed:** We have made the UI for Housemate more clear and easy to understand by removing the calendar tab https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/45 
- **User Login Memorization:** If a user does not log out and closes the app they remain logged in https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/41

**_Enhancements_**
- Streamlined navigation and user interface for an improved overall user experience, making it easier for users to access their households, manage chores, and keep track of expenses.
- Enhanced security features to ensure the safety and privacy of user data, with robust encryption for sensitive information and improved authentication processes.
- Upgraded group visibility and management capabilities, allowing users to easily see their household names and group codes, switch groups, and manage group membership directly from the app.
- Improved chore management system, including the ability to delete chores, set repeat frequencies, and view upcoming chores on the HomeScreen for better organization.
- Refined expenses tracking and management, with a comprehensive summary connected to Firestore, streamlined expense splitting, and the introduction of a user-friendly 'Settle Up' UI.
- Optimized user experience with the introduction of personalized household naming upon registration, a choice of username during account setup, and the memorization of login status for convenience.
- Enhanced chore and expense feedback mechanisms, including the ability to rate chores based on quality and view a detailed history of expenses for better group dynamics and financial transparency.
- Simplified app interface by removing the calendar tab, focusing on core functionalities to enhance usability and clarity for all users.

**_Download Installer_**
Download our installer for HouseMate here: 

For a full list of changes, visit our issues list on GitLab: https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues

## Release Notes Version 1.1.0
**_What's New_**
- **Authentication Enhancements:** Redesigned the authentication UI and backend mechanisms to provide a smoother login and sign-up experience, increasing both security and user-friendliness https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/33
- **Brand Identity:** Introduced our new logo to better reflect our brand identity and values, enhancing the app's aesthetic and user connection. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/32
- **User Settings:** Launched a new settings screen UI, allowing users to customize their experience and manage their accounts more effectively. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/30
- **Data Management:** Initialized Cloud Firestore for enhanced data storage, retrieval, and real-time updates, ensuring that user data remains secure and accessible. Now, user IDs are also securely stored in the cloud on Firestore, ensuring a seamless and secure user experience. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/29
- **System Stability:** Addressed and resolved an issue causing the app to crash, improving overall stability and performance. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/28
- **Database Integration:** Successfully connected Firestore, enabling better data synchronization and integrity across the app. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/26
- **Create Chore Screen and Flow:** Currently enhancing the process for creating and managing chores within the app. Expected to make task tracking more intuitive and user-friendly. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/27

**_In Progress (Expected in Next Release)_**
- **Household Setup Screen:** Improving the setup screen for joining or creating households, aimed at simplifying the process and enhancing user engagement. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/25
- **User Profile Management:** Implementing read and update operations for user profiles to provide a more personalized experience. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/16
- **Group Management:** Enhancing features to allow users to create and join groups/households more easily, fostering community and collaboration. https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/2

**_Enhancements_**
- Streamlined navigation and user interface for an improved overall user experience.
- Enhanced security features to ensure the safety and privacy of user data.

**_Download Installer_**
Download our installer for HouseMate here: https://git.uwaterloo.ca/gmmoroie/team-101-13/-/blob/main/housemate-sprint2.apk

For a full list of changes, visit our issues list on GitLab: 

## Release Notes Version 1.0.0
**_What's New_**
- **Login screen UI:** Introduced a new login screen, enhancing user experience and security https://git.uwaterloo.ca/gmmoroie/team-101-13/-/work_items/19
- **Sign up screen UI:** Added a sign-up screen, making it easier for new users to join HouseMate https://git.uwaterloo.ca/gmmoroie/team-101-13/-/work_items/18
- **Navigation Bar:** Implemented a navigation bar for improved app navigation https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/17
- **Calendar screen UI:** Developed a Calendar UI, facilitating better planning and tracking of important dates and events for users https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/22
- **Expenses UI:** Created expenses Ui, simplifying the process of tracking and splitting household expenses among roommates https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/20
- **Firebase Authentication:** Added FireBase for user profiles/authentication https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/14

**_Enhancements_**
- Improved UI/UX for easier navigation
- Enhanced security measures for user data

**_Download Installer_**
Download our installer for HouseMate here: https://git.uwaterloo.ca/gmmoroie/team-101-13/-/blob/main/housemate-sprint1.apk

For a full list of changes, visit our issues list on GitLab: https://git.uwaterloo.ca/gmmoroie/team-101-13/-/issues/?sort=created_date&state=closed&first_page_size=20

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.


