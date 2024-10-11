# Bookswap

## Description
The application would consist of an online version of the free book exchange boxes often found in small villages scattered throughout the whole of the Swiss countryside, it would help avoid having to search for said box exchanges and would additionally allow a search for a more desirable book than what perhaps would be in the box if one were to simply walk up to said box and have a look.

The intended target audience are the older folk who most often use said exchange boxes and avid readers who may either not have easy access to the boxes or who do, yet prefer looking for books that may cater to them.

It would include a repository of books that you have read and are willing to exchange, this is a list or repository of book entries each containing a unique bookID (generated upon creation, unique to each entry), a title, an author, a back cover text, a series of tags on the subject, an optional note on opinions about the book and a rating by the entry owner. 

We would additionally include a functionality by which one can take two pictures of the front and back covers and it would automatically fill in all information, this is to aid mainly with the back cover description, as they may be long and lengthy enough to consider an annoyance having to write it in a text box on the app.

Next there is the recommendation version, one can click on tags (at least one is mandatory) on the genre of the book, this would allow that upon a good opinion of a book, the app will recommend using an AI the closest books that match the genres you like. This would of course use geolocation as it must match you with books that lie somewhere in your near vicinity. 

There is also an AI that takes in a request that can be somewhat vague and come up with better books according to your current mood and desires.

Lastly, there is a method of connecting with other people to propose the book exchange, or maybe something further… Each account has a few things, mostly based on the google login, which would allow you to enter a chatbox with the people you want a book from, it would be a very simple chatbox, with minimal memory of previous texts, but long enough to set up a meeting and hopefully provide you with a new book and perhaps a new close acquaintance.

- For the split app model, we will use the public cloud services not only for the google login, but also for storing the repository of books and the chatbox.
- A google login will be used to share info between devices.
- It will use geolocation for the recommendation and camera for easier creation of book entries
- The scans of the books and entry creation and managing will be offline and doable without connectivity, it will also store a state so you can see your directory as it was the last time the android was connected.It will update the cloud upon reconnecting.

[Figma link](https://www.figma.com/design/uyHS0PV5RBnmToK2JuTb4h/Untitled?node-id=0-1&m=dev&t=VdHNM8v7U5bhvsW7-1)
