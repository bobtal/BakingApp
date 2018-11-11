package com.gmail.at.boban.talevski.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.model.Step;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.gmail.at.boban.talevski.bakingapp.ui.RecipeListFragment.EXTRA_RECIPE;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<RecipeDetailsActivity>(RecipeDetailsActivity.class) {

        /**
         * set up Intent as if supplied to RecipeDetailsActivity
         */
        @Override
        protected Intent getActivityIntent() {
            Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

            // Set up a test Recipe object to send with an intent to RecipeDetailsActivity

            // not interested in recipeId
            int recipeId = -1;
            String recipeName = "I am recipe name";
            // lots of ingredients to make the last one(s) not visible, so need to scroll first
            // before checking assertions
            List<Ingredient> ingredientList = Arrays.asList(
                    new Ingredient(1, "measure1", "ingredient1"),
                    new Ingredient(1, "measure2", "ingredient2"),
                    new Ingredient(1, "measure3", "ingredient3"),
                    new Ingredient(1, "measure4", "ingredient4"),
                    new Ingredient(1, "measure5", "ingredient5"),
                    new Ingredient(1, "measure6", "ingredient6"),
                    new Ingredient(1, "measure7", "ingredient7"),
                    new Ingredient(1, "measure8", "ingredient8"),
                    new Ingredient(1, "measure9", "ingredient9"),
                    new Ingredient(1, "measure10", "ingredient10")
            );
            List<Step> stepList = Arrays.asList(
                    new Step(1, "shortdescription1", "longdescription1",
                            "somevideourl", "somethumbnailurl"),
                    new Step(1, "shortdescription2", "longdescription2",
                            "somevideourl", "somethumbnailurl"),
                    new Step(1, "shortdescription3", "longdescription3",
                            "somevideourl", "somethumbnailurl"),
                    new Step(1, "shortdescription4", "longdescription4",
                            "somevideourl", "somethumbnailurl")
            );
            // not interested in servings
            int servings = -1;
            // not interested in imageUrl
            String imageUrl = "recipeimageurl";
            Recipe recipe = new Recipe(recipeId, recipeName, ingredientList, stepList, servings, imageUrl);

            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra(EXTRA_RECIPE, recipe);
            return intent;
        }
    };

    @Test
    public void incomingRecipeProperlyPopulatesIngredientsDataInRecipeDetailsActivity() {
        // checks if the item in the last position (9) in the ingredients recycler view
        // has the appropriate value from the sent intent. Need to scroll to that position
        // as it's most likely not visible
        onView(withId(R.id.recipe_ingredients_recycler_view))
                .perform(scrollToPosition(9))
                .check(matches(atPosition(9, hasDescendant(withText("Ingredient10 - measure10 1.00")))));
    }

    @Test
    public void incomingRecipeProperlyPopulatesStepsDataInRecipeDetailsActivity() {
        // checks if the item in the last position (4) in the steps recycler view
        // has the appropriate value from the sent intent. Need to scroll to that position
        // as it could be not visible on eiter phone or tablet
        onView(withId(R.id.recipe_steps_recycler_view))
                .perform(scrollToPosition(3))
                .check(matches(atPosition(3, hasDescendant(withText("shortdescription4")))));
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

}