# Milestone M2: Team Feedback

This milestone M2 provides an opportunity to give you, as a team, formal feedback on how you are performing in the project. By now, you should be building upon the foundations set in M1, achieving greater autonomy and collaboration within the team. This is meant to complement the informal, ungraded feedback from your coaches given during the weekly meetings or asynchronously on Discord, email, etc.

The feedback focuses on two major themes:
First, whether you have adopted good software engineering practices and are making progress toward delivering value to your users.
Is your design and implementation of high quality, easy to maintain, and well tested?
Second, we look at how well you are functioning as a team, how you organize yourselves, and how well you have refined your collaborative development.
An important component is also how much you have progressed, as a team, since the previous milestone.
You can find the evaluation criteria in the [M2 Deliverables](https://github.com/swent-epfl/public/blob/main/project/M2.md) document.
As mentioned in the past, the standards for M2 are elevated relative to M1, and this progression will continue into M3.

We looked at several aspects, grouped as follows:

 - Design
   - [Features](#design-features)
   - [Design Documentation](#design-documentation)
 - [Implementation and Delivery](#implementation-and-delivery)
 - Scrum
   - [Backlogs Maintenance](#scrum-backlogs-maintenance)
   - [Documentation and Ceremonies](#scrum-documentation-and-ceremonies)
   - [Continuous Delivery of Value](#scrum-continuous-delivery-of-value)

## Design: Features

We interacted with your app from a user perspective, assessing each implemented feature and flagging any issues encountered. Our evaluation focused mainly on essential features implemented during Sprints 3, 4, and 5; any additional features planned for future Sprints were not considered in this assessment unless they induced buggy behavior in the current APK.
We examined the completeness of each feature in the current version of the app, and how well it aligns with user needs and the overall project goals.


The features implemented in the app are very interesting. The chat feature is nicely done and allows multiple users to interact.
The map correctly recognizes the localization of the user and you also use the camera sensor for either the chat or the future use of chatgpt. The modularization of your code will allow you to make the next implementations easier. So good job!
The features already implemented in your app are almost complete which is very nice but try to spot any bugs you may have and make sure to handle the faulty behaviour in your tests to make your main branch release-ready at all times. Nevertheless, you chose impactful features aligned with the app's core goals, including to add books easily and interact with other users. They each serve a clear purpose so congratulations for your work !


For this part, you received 7.5 points out of a maximum of 8.0.

## Design: Documentation

We reviewed your Figma (including wireframes and mockups) and the evolution of your overall design architecture in the three Sprints.
We assessed how you leveraged Figma to reason about the UX, ensure a good UX, and facilitate fast UI development.
We evaluated whether your Figma and architecture diagram accurately reflect the current implementation of the app and how well they align with the app's functionality and structure.


The Figma is of great quality and represents well the current state of the app even though some little details are missing like the button to take a picture in the chat screen.
It is always good to have a few future features already implemented in the Figma so that the whole team gets a visual of how it would look like. Nevertheless, even back in M1, you did a good job in your Figma. Keep it up! 

Concerning the architecture diagram, it provides a clear and precise architecture of the app. You are accounting for all the features you plan to implement which is a good practice. Good job! However, you should add either a caption or a small text in the side to explain what the different colors of the arrows refer to. 


For this part, you received 5.7 points out of a maximum of 6.0.

## Implementation and Delivery

We evaluated several aspects of your app's implementation, including code quality, testing, CI practices, and the functionality and quality of the APK.
We assessed whether your code is well modularized, readable, and maintainable.
We looked at the efficiency and effectiveness of your unit and end-to-end tests, and at the line coverage they achieve.


**Code Quality**
Your repository is well-organized. The MVVM architecture is mostly respected in the whole project. Having a folder regrouping the components used multiple times throughout the app is a very good practice. Good job. 
You should try to be more consistent in the documentation of your code as some classes are well-documented and others not so much. It is always helpful for you and for future developers to understand what each function does and explain complicated logic. 
As said in M1, you should avoid merging commented out code and using magic numbers but it is happening less frequently than before so good job on that. 
However, a remark that needs to be taken into account is to not display dummy data on the MainActivity directly or other classes. There were some instances of hard-coded values like uuids that can be found directly in the screens and this should be avoided. As you probably already know, the main branch should always stay clean and release-ready for the best maintainabiliy. So try to take that into account to manage to finish the backend and UI at the same time or simply do not merge the feature in main and keep it in a feature branch until it is completely ready for main.
Nevertheless, you have made some strong improvements from M1 where you follow more closely the MVVM architecture and develop good coding practices. Great job!

**Test and CI**
You succeeded in achieving a line coverage of 85% in the project which is very good ! You could reach even more if you test edge cases like null or empty inputs. This was lacking in some classes but most of the tests were meaningful.
You have succeeded in delivering a total of more than 2 end-to-end tests as requested by the M2 Deliverables. Here are some remarks :
- Add Books e2e: The test covers two main flows for adding a book: using ISBN lookup and manual entry, which is good. The test does not simulate or validate failure cases, such as an invalid ISBN leading to an error or missing required fields in the manual entry form. Adding those cases improves the robustness of your code. 
- Chat e2e: Your test case demonstrates an impressive level of detail and comprehensiveness. You could break the test into smaller methods for each feature (e.g., testSendMessage, testEditMessage, testDeleteMessage) as this would improve readability and maintainability. It is easier this way to adress specific failures. Also, while the mocks are robust, introducing edge cases (e.g., simulate failures in sendMessage or deleteMessage) would test the app's resilience. Nevertheless, this is impressive work. Good job!
An important reminder would be to start your end-to-end tests in the mainActivity of your app to show a real user flow. You should mpck what a user does from the second he/she enters the app. 

**APK Functionality and Performance**
- The Welcome page is very well made. The input sanitization is good and the user flow is great
- The first page is the Map page but the localization is not working even with the given permissions. Only a blue screen is showed. It is only after applying filters that the map appeared.
- It is not possible to click on the suggested books in the map screen. If it is a feature that is not implemented, then a toast would be helpful so that the user knows that it is either impossible or the feature is being implemented. Some back buttons also have the same behaviour. The "From Photo" page should be blocked rather than send the user to a blank page. 
- There is a back button on the Map page and when clicking on it, we get back to the welcome page and are obliged to refill all the fields.
- The chat feature is well made. When taking a picture, there is no preview of it in the text placeholder before clicking on the button "send". This could be added to improve the user experience. When clicking on modify a picture message, we are given the option to modify the URL. It would be better if you could just resend the user to the camera screen. Also, make sure to prevent users from sending empty messages as it is possible right now.
- When wanting to add a book, we didn't succeed because the inputted language was said to be invalid but no information was given about the format expected. 
In summary, the APK functions adequately and can easily be used. I has some minor bugs but they lightly affect the user experience.


For this part, you received 13.2 points out of a maximum of 16.0.

## Scrum: Backlogs Maintenance

We looked at whether your Scrum board is up-to-date and well organized.
We evaluated your capability to organize Sprint 6 and whether you provided a clear overview of this planning on the Scrum board.
We assessed the quality of your user stories and epics: are they clearly defined, are they aligned with a user-centric view of the app, and do they suitably guide you in delivering the highest value possible.


Many tasks for sprint 6 did not have a time estimation nor a priority tag, which are important informations for your to know exactly how the sprint would go. There are also members that were assigned only one task which is not enough for an 8 credit course.
Also, when finishing a user story, you should put it in the Done column of the sprint where it was finished so that you product backlog is always up-to-date and you know exactly what is left to do.
Nevertheless, some of your tasks have description when the title is not clear enough and your product backlog covers all the app features which is very good.


For this part, you received 2.2 points out of a maximum of 4.0.

## Scrum: Documentation and Ceremonies

We assessed how you used the Scrum process to organize yourselves efficiently.
We looked at how well you documented your team Retrospective and Stand-Up during each Sprint.
We also evaluated your autonomy in using Scrum.


**SCRUM Documents**
The standup meetings are supposed to be done together and led by the Scrum Master. These meetings are there to first of all, set a first deadline for everyone to show their progress and not let everything at the last minute, but also to know exactly what were the problems if any occurred so that the team members can help each other if necessary. So, you should  not fill out the standup meeting document each on your own but rather do the meeting together and fill it during the meeting. Apart from that, you fill the team documents quite thoroughly which is very good. Great job!

**SCRUM Meetings**
The meetings are well-structured and most of the team members show real engagement during the meetings. You always ask questions or discuss about how to improve certain features or fix some problems which shows your seriousness and professionalism.

**Autonomy**
You have showed strong autonomy starting from Sprint 3. We rarely need to intervene as you are a very efficient and dynamix of leading the meetings. Most of the Scrum Masters and Product Owners did a very good job. The meetings are organized and the SCRUM process is very well-managed. This is excellent work!


For this part, you received 4 points out of a maximum of 4.0.

## Scrum: Continuous Delivery of Value

We evaluated the Increment you delivered at the end of each Sprint, assessing your team’s ability to continuously add value to the app.
This included an assessment of whether the way you organized the Sprints was conducive to an optimal balance between effort invested and delivery of value.


You managed to deliver consistent value to your app throughout the sprints. Even when some of you had some midterms, you still managed to organize yourselves and tried to get the best out of the sprint. 
A perfect sprint would be one taking into account multiple aspects of the app that need to be improved like implementation of features, UI/UX designs, documentation and code quality. Showing your coaches a demo is the cherry on top to demonstrate the concrete improvements in the app. 


For this part, you received 1.8 points out of a maximum of 2.0.

## Summary

Based on the above points, your intermediate grade for this milestone M2 is 5.30. If you are interested in how this fits into the bigger grading scheme, please see the [project README](https://github.com/swent-epfl/public/blob/main/project/README.md) and the [course README](https://github.com/swent-epfl/public/blob/main/README.md).

Your coaches will be happy to discuss the above feedback in more detail.

Good luck for the next Sprints!
