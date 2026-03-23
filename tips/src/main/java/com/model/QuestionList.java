package com.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manages the collection of all questions in the system.
 * Owns all question-level business logic including filtering,
 * adding, removing, and delegating to individual questions.
 * Implemented as a singleton.
 * @author Thomas Dunn, James Gessler
 */
public class QuestionList {
    private static QuestionList instance;
    private ArrayList<Question> questions;

    /**
     * Constructs the QuestionList by loading all questions from the JSON file.
     */
    private QuestionList() {
        questions = DataLoader.getQuestions();
    }

    /**
     * Returns the single instance of QuestionList, creating it if needed.
     * @return the QuestionList instance
     */
    public static QuestionList getInstance() {
        if (instance == null) instance = new QuestionList();
        return instance;
    }

    /**
     * Returns all questions, optionally filtered by a keyword.
     * Matches against title, prompt, difficulty, language, or course.
     * Passing null or blank returns all questions.
     * @param filter the keyword to filter by, or null for all questions
     * @return a list of matching questions
     */
    public ArrayList<Question> getQuestions(String filter) {
        if (filter == null || filter.trim().isEmpty()) return getAllQuestions();
        ArrayList<Question> results = new ArrayList<>();
        String lower = filter.toLowerCase();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(lower)
                    || q.getPrompt().toLowerCase().contains(lower)
                    || q.getDifficulty().name().equalsIgnoreCase(filter)
                    || q.getLanguage().name().equalsIgnoreCase(filter)
                    || q.getCourse().name().equalsIgnoreCase(filter)) {
                results.add(q);
            }
        }
        return results;
    }

    /**
     * Returns the first question whose title or prompt contains the given keyword.
     * @param keyword the keyword to search for
     * @return the matching question, or null if none found
     */
    public Question getQuestion(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        String lower = keyword.toLowerCase();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(lower)
                    || q.getPrompt().toLowerCase().contains(lower)) return q;
        }
        return null;
    }

    /**
     * Returns a copy of all questions in the list.
     * @return all questions
     */
    public ArrayList<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    /**
     * Returns a daily challenge question tailored to a student.
     * Picks a question matching the student's classification difficulty level
     * that the student has not yet submitted a solution for.
     * Falls back to any unsolved question if no match is found.
     * @param student the student to find a challenge for
     * @return a suitable question, or null if none available
     */
    public Question getDailyChallenge(Student student) {
        Difficulty targetDifficulty = classificationToDifficulty(student.getClassification());

        // First pass: matching difficulty with no submission from this student
        for (Question q : questions) {
            if (q.getDifficulty() == targetDifficulty && !hasSubmitted(q, student)) {
                return q;
            }
        }
        // Fallback: any question with no submission from this student
        for (Question q : questions) {
            if (!hasSubmitted(q, student)) return q;
        }
        return null;
    }

    /**
     * Maps a student classification to an appropriate difficulty level.
     * @param classification the student's classification string
     * @return the matching Difficulty
     */
    private Difficulty classificationToDifficulty(String classification) {
        if (classification == null) return Difficulty.EASY;
        switch (classification.toLowerCase()) {
            case "junior":
            case "senior":   return Difficulty.HARD;
            case "sophomore": return Difficulty.MEDIUM;
            default:          return Difficulty.EASY;
        }
    }

    /**
     * Returns whether a student has already submitted a solution for a question.
     * @param question the question to check
     * @param student the student to check for
     * @return true if the student has a submission, false otherwise
     */
    private boolean hasSubmitted(Question question, Student student) {
        for (Solution s : question.getSolutions()) {
            if (s.getAuthor() != null && s.getAuthor().getUserId() == student.getUserId())
                return true;
        }
        return false;
    }

    /**
     * Prints the full details of a question to a text file.
     * Includes the prompt, hint, solutions, and all comments.
     * @param question the question to print
     * @param filename the output file path
     */
    public void printQuestionToFile(Question question, String filename) {
        if (question == null) return;
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("============================================\n");
            fw.write("Title:      " + question.getTitle() + "\n");
            fw.write("Course:     " + question.getCourse() + "\n");
            fw.write("Difficulty: " + question.getDifficulty() + "\n");
            fw.write("Language:   " + question.getLanguage() + "\n");
            if (question.getAuthor() != null)
                fw.write("Author:     " + question.getAuthor().getUsername() + "\n");
            fw.write("============================================\n\n");

            fw.write("PROMPT:\n");
            fw.write(question.getPrompt() + "\n\n");

            if (question.getHint() != null && !question.getHint().isEmpty()) {
                fw.write("HINT:\n");
                fw.write(question.getHint() + "\n\n");
            }

            if (!question.getSolutions().isEmpty()) {
                fw.write("--------------------------------------------\n");
                fw.write("SOLUTIONS (" + question.getSolutions().size() + "):\n");
                fw.write("--------------------------------------------\n");
                int i = 1;
                for (Solution s : question.getSolutions()) {
                    String author = s.getAuthor() != null ? s.getAuthor().getUsername() : "unknown";
                    String accepted = s.isAccepted() ? " [ACCEPTED]" : "";
                    fw.write("\nSolution " + i++ + " by " + author
                            + " (" + s.getUpvotes() + " upvotes)" + accepted + ":\n");
                    fw.write(s.getContent() + "\n");
                }
                fw.write("\n");
            }

            if (question.isSolutionRevealed() && question.getSampleSolution() != null) {
                fw.write("--------------------------------------------\n");
                fw.write("SAMPLE SOLUTION:\n");
                fw.write(question.getSampleSolution() + "\n\n");
                fw.write("EXPLANATION:\n");
                fw.write(question.getSampleExplanation() + "\n\n");
            }

            if (!question.getReplies().isEmpty()) {
                fw.write("--------------------------------------------\n");
                fw.write("COMMENTS:\n");
                fw.write("--------------------------------------------\n");
                for (Reply r : question.getReplies()) {
                    writeReply(fw, r, 0);
                }
            }

            fw.write("\n============================================\n");
            fw.flush();
            System.out.println("Question printed to: " + filename);
        } catch (IOException e) {
            System.out.println("Error writing question to file: " + e.getMessage());
        }
    }

    /**
     * Writes a reply and its nested replies to a FileWriter recursively.
     * @param fw the FileWriter to write to
     * @param reply the reply to write
     * @param depth the current indentation depth
     */
    private void writeReply(FileWriter fw, Reply reply, int depth) throws IOException {
        String indent = "  ".repeat(depth);
        fw.write(indent + "[" + reply.getAuthor().getUsername() + "]"
                + " " + reply.getTitle()
                + " - " + reply.getDatePosted() + "\n");
        fw.write(indent + reply.getContent() + "\n\n");
        for (Reply nested : reply.getReplies()) {
            writeReply(fw, nested, depth + 1);
        }
    }

    /**
     * Creates and adds a new question to the list.
     * Only Editors and Admins are allowed to add questions.
     * @param title the question title
     * @param prompt the question prompt
     * @param difficulty the difficulty level
     * @param language the programming language
     * @param course the associated course
     * @param author the user creating the question
     * @return the created Question, or null if the author is not authorized
     */
    public Question addQuestion(String title, String prompt, Difficulty difficulty,
            Language language, Course course, User author) {
        if (author == null) return null;
        if (!(author instanceof Editor) && !(author instanceof Admin)) {
            System.out.println("Only Editors and Admins can add questions.");
            return null;
        }
        Question q = new Question(title, prompt, difficulty, language, course);
        q.setAuthor(author);
        questions.add(q);
        return q;
    }

    /**
     * Adds an already-created question directly to the list.
     * @param q the question to add
     */
    public void addQuestion(Question q) {
        if (q != null && !questions.contains(q)) questions.add(q);
    }

    /**
     * Removes a question from the list.
     * Only Admins are allowed to remove questions.
     * @param q the question to remove
     * @param requestingUser the user requesting the removal
     * @return true if removed, false if not authorized or not found
     */
    public boolean removeQuestion(Question q, User requestingUser) {
        if (!(requestingUser instanceof Admin)) {
            System.out.println("Only Admins can remove questions.");
            return false;
        }
        return questions.remove(q);
    }

    /**
     * Removes a question directly without a permission check.
     * @param q the question to remove
     */
    public void removeQuestion(Question q) {
        questions.remove(q);
    }

    /**
     * Submits a code solution to a question.
     * Delegates to Question and saves if successful.
     * @param question the question being answered
     * @param author the user submitting the solution
     * @param content the submitted code
     * @return the created Solution, or null if submission failed
     */
    public Solution submitSolution(Question question, User author, String content) {
        if (question == null) return null;
        Solution solution = question.submitSolution(author, content);
        if (solution != null) DataWriter.saveQuestions();
        return solution;
    }

    /**
     * Adds a top-level comment to a question.
     * Delegates to Question and saves if successful.
     * @param question the question being commented on
     * @param author the user posting the comment
     * @param title the comment title
     * @param content the comment text
     * @return the created Reply, or null if it failed
     */
    public Reply addComment(Question question, User author, String title, String content) {
        if (question == null) return null;
        Reply reply = question.addComment(author, title, content);
        if (reply != null) DataWriter.saveQuestions();
        return reply;
    }

    /**
     * Adds a nested reply to an existing comment on a question.
     * Delegates to Question and saves if successful.
     * @param question the question the comment belongs to
     * @param parentReply the reply being responded to
     * @param author the user posting the nested reply
     * @param title the reply title
     * @param content the reply text
     * @return the created Reply, or null if it failed
     */
    public Reply addNestedReply(Question question, Reply parentReply, User author,
            String title, String content) {
        if (question == null) return null;
        Reply reply = question.addNestedReply(parentReply, author, title, content);
        if (reply != null) DataWriter.saveQuestions();
        return reply;
    }

    /**
     * Removes a comment from a question.
     * Delegates to Question and saves if successful.
     * @param question the question the comment belongs to
     * @param comment the reply to remove
     * @param requestingUser the user requesting the removal
     * @return true if removed, false if not authorized or not found
     */
    public boolean removeComment(Question question, Reply comment, User requestingUser) {
        if (question == null) return false;
        boolean removed = question.removeComment(comment, requestingUser);
        if (removed) DataWriter.saveQuestions();
        return removed;
    }

    /**
     * Reveals the sample solution for a question and saves the change.
     * @param question the question whose solution to reveal
     */
    public void revealSolution(Question question) {
        if (question == null) return;
        question.revealSolution();
        DataWriter.saveQuestions();
    }

    /**
     * Saves all questions to the JSON file.
     */
    public void save() {
        DataWriter.saveQuestions();
    }
}
