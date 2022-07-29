package com.sisiame.wordal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.*
import android.view.View

/**
 * This program is a simple clone of the popular New York Times game,
 * Wordle. Players have 3 chances to guess a random 4 letter word.
 *
 * @author Sisiame B. Sakasamo
 *
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val BLANK_LETTER: Int = 0
        private const val INVALID_CHARACTER: Int = 1
    }

    // each individual letter is a separate input, with every EditText stored in the inputs array
    private lateinit var inputs: Array<EditText>

    // when valid guesses are submitted, the words are displayed using these TextViews
    private lateinit var outputs: Array<TextView>

    private lateinit var winLoseText: TextView

    // the randomly selected word for the round
    private lateinit var wordToGuess: String

    private lateinit var submitButton: ImageButton
    private lateinit var restartButton: ImageButton

    // the number of guesses the player has used so far
    private var numGuesses: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // gets each indiviudal EditText for each input's letter for the inputs array
        inputs = arrayOf(findViewById(R.id.input1), findViewById(R.id.input2),
            findViewById(R.id.input3), findViewById(R.id.input4))

        // gets each individual TextView for each output's letter for the outputs array
        outputs = arrayOf(findViewById(R.id.guess1), findViewById(R.id.guess2),
            findViewById(R.id.guess3))

        // finds views and assigns them to fields
        winLoseText = findViewById(R.id.winLoseText)
        submitButton = findViewById(R.id.submit)
        restartButton = findViewById(R.id.restart)

        // sets click listeners for buttons
        submitButton.setOnClickListener { submitGuess() }
        restartButton.setOnClickListener { startGame() }

        // initializes the game
        startGame()
    }

    /**
     * Initializes the game.
     */
    private fun startGame() {

        // resets number of guesses
        numGuesses = 0

        // hides the restart button
        restartButton.visibility = View.GONE

        // resets the game status text
        winLoseText.text = ""

        // resets outputs
        for(output in outputs) {
            output.text = getString(R.string.output_placeholder)
        }

        // selects a new word
        wordToGuess = FourLetterWordList.getRandomFourLetterWord()
    }

    /**
     * Submits the player's guess if the input is valid.
     */
    private fun submitGuess() {

        if(validateGuessInput()) {

            var outputString = ""

            // puts together the output string using each letter input
            for(letter in inputs) {
                val input = letter.text.toString()
                outputString = "$outputString$input"
                letter.clearFocus()
                letter.text.clear()
            }

            outputs[numGuesses].text = outputString

            // increases number of guesses each time the player submits a valid guess
            numGuesses++

            checkWin(outputString)
        }

    }

    /**
     * Checks if inputs are valid.
     * Inputs are valid if they contain only letters.
     *
     * @return whether the inputs are valid or not
     */
    private fun validateGuessInput(): Boolean {

        // loops through each letter inputted
        for(letterInput in inputs) {

            // convert Editable to String for use with String functions
            val letter = letterInput.text.toString()

            // checks if any inputs are blank or empty
            if(letter.isBlank()) {
                showErrorMessage(BLANK_LETTER)
                return false
            }

            // checks if any inputs are not letters
            if((letter !in "A".."Z" && letter !in "a".."z")) {
                showErrorMessage(INVALID_CHARACTER)
                return false
            }

        }

        return true

    }

    /**
     * Displays an error message as a toast.
     * Intended for use with input validator.
     *
     * @param errorCode the number code associated with the error
     *
     */
    private fun showErrorMessage(errorCode: Int) {

        if(errorCode == BLANK_LETTER) {
            // shows message if one of the inputs are blank
            Toast.makeText(this, "You're missing letters!", Toast.LENGTH_SHORT).show()
        } else if(errorCode == INVALID_CHARACTER) {
            // shows message if one of the inputs is not a letter
            Toast.makeText(this, "You can only enter letters!", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Checks if the player won the game.
     *
     * @param guess the player's inputted word
     */
    private fun checkWin(guess: String) {

        if(guess == wordToGuess) {
            winGame()
        } else if(numGuesses >= 3) {
            loseGame()
        }

        setLetterColors(guess)

    }

    /**
     * Displays a victory message and ends the game.
     */
    private fun winGame() {
        winLoseText.setTextColor(getColor(R.color.green))
        winLoseText.text = resources.getStringArray(R.array.win)[numGuesses - 1]
        restartButton.visibility = View.VISIBLE
    }

    /**
     * Displays a failure message and ends the game.
     */
    private fun loseGame() {
        winLoseText.setTextColor(getColor(R.color.red))

        // shows what the correct word was meant to be
        winLoseText.text = String.format(getString(R.string.lose), wordToGuess)
        restartButton.visibility = View.VISIBLE
    }

    /**
     * Sets the colors of the outputs' letters based on correctness.
     * Green represents a correctly placed letter.
     * Red represents an incorrectly placed letter.
     *
     * @param guess the player's inputted word
     */
    private fun setLetterColors(guess: String) {

        // creates a SpannableString with the guessed word to color each individual letter
        val outputSpannable = SpannableString(guess)

        // colors correctly placed letters green and incorrectly placed letters red
        for(letter in 0..3) {

            if(wordToGuess.contains(guess[letter], true)) {

                if(wordToGuess[letter].equals(guess[letter], true)) {
                    outputSpannable.setSpan(
                        ForegroundColorSpan(getColor(R.color.green)),
                        letter, letter + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    outputSpannable.setSpan(
                        ForegroundColorSpan(getColor(R.color.red)),
                        letter, letter + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

            }

        }

        outputs[numGuesses - 1].text = outputSpannable

    }

    /**
     * Stores all the 4 letter words to be selected at random for
     * the game.
     *
     * @author calren, Sisiame B. Sakasamo
     *
     */
    object FourLetterWordList {

        // List of most common 4 letter words from: https://7esl.com/4-letter-words/
        private const val fourLetterWords = "Area,Army,Baby,Back,Ball,Band,Bank,Base,Bill,Body,Book,Call,Card,Care,Case,Cash,City,Club,Cost,Date,Deal,Door,Duty,East,Edge,Face,Fact,Farm,Fear,File,Film,Fire,Firm,Fish,Food,Foot,Form,Fund,Game,Girl,Goal,Gold,Hair,Half,Hall,Hand,Head,Help,Hill,Home,Hope,Hour,Idea,Jack,John,Kind,King,Lack,Lady,Land,Life,Line,List,Look,Lord,Loss,Love,Mark,Mary,Mind,Miss,Move,Name,Need,News,Note,Page,Pain,Pair,Park,Part,Past,Path,Paul,Plan,Play,Post,Race,Rain,Rate,Rest,Rise,Risk,Road,Rock,Role,Room,Rule,Sale,Seat,Shop,Show,Side,Sign,Site,Size,Skin,Sort,Star,Step,Task,Team,Term,Test,Text,Time,Tour,Town,Tree,Turn,Type,Unit,User,View,Wall,Week,West,Wife,Will,Wind,Wine,Wood,Word,Work,Year,Bear,Beat,Blow,Burn,Call,Care,Cast,Come,Cook,Cope,Cost,Dare,Deal,Deny,Draw,Drop,Earn,Face,Fail,Fall,Fear,Feel,Fill,Find,Form,Gain,Give,Grow,Hang,Hate,Have,Head,Hear,Help,Hide,Hold,Hope,Hurt,Join,Jump,Keep,Kill,Know,Land,Last,Lead,Lend,Lift,Like,Link,Live,Look,Lose,Love,Make,Mark,Meet,Mind,Miss,Move,Must,Name,Need,Note,Open,Pass,Pick,Plan,Play,Pray,Pull,Push,Read,Rely,Rest,Ride,Ring,Rise,Risk,Roll,Rule,Save,Seek,Seem,Sell,Send,Shed,Show,Shut,Sign,Sing,Slip,Sort,Stay,Step,Stop,Suit,Take,Talk,Tell,Tend,Test,Turn,Vary,View,Vote,Wait,Wake,Walk,Want,Warn,Wash,Wear,Will,Wish,Work,Able,Back,Bare,Bass,Blue,Bold,Busy,Calm,Cold,Cool,Damp,Dark,Dead,Deaf,Dear,Deep,Dual,Dull,Dumb,Easy,Evil,Fair,Fast,Fine,Firm,Flat,Fond,Foul,Free,Full,Glad,Good,Grey,Grim,Half,Hard,Head,High,Holy,Huge,Just,Keen,Kind,Last,Late,Lazy,Like,Live,Lone,Long,Loud,Main,Male,Mass,Mean,Mere,Mild,Nazi,Near,Neat,Next,Nice,Okay,Only,Open,Oral,Pale,Past,Pink,Poor,Pure,Rare,Real,Rear,Rich,Rude,Safe,Same,Sick,Slim,Slow,Soft,Sole,Sore,Sure,Tall,Then,Thin,Tidy,Tiny,Tory,Ugly,Vain,Vast,Very,Vice,Warm,Wary,Weak,Wide,Wild,Wise,Zero,Ably,Afar,Anew,Away,Back,Dead,Deep,Down,Duly,Easy,Else,Even,Ever,Fair,Fast,Flat,Full,Good,Half,Hard,Here,High,Home,Idly,Just,Late,Like,Live,Long,Loud,Much,Near,Nice,Okay,Once,Only,Over,Part,Past,Real,Slow,Solo,Soon,Sure,That,Then,This,Thus,Very,When,Wide"

        /**
         * Returns all the 4 letter words as a list.
         */
        private fun getAllFourLetterWords(): List<String> {
            return fourLetterWords.split(",")
        }

        /**
         * Selects a random word from the list of 4 letter words.
         *
         * @return a word from the list of 4 letter words
         *
         */
        fun getRandomFourLetterWord(): String {
            val allWords = getAllFourLetterWords()
            val randomNumber = (allWords.indices).random()
            return allWords[randomNumber].uppercase()
        }
    }

}