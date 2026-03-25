package com.example.socialfeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Social media feed app with Material Design.
 * Profile avatars, post cards, likes/comments, stories bar, image posts.
 */
public class SocialFeedApp {

    // ═══ Theme ═══
    static final int PRIMARY = 0xFF1877F2;    // Facebook blue
    static final int BG = 0xFFF0F2F5;
    static final int WHITE = 0xFFFFFFFF;
    static final int DARK = 0xFF1C1E21;
    static final int SECONDARY = 0xFF65676B;
    static final int DIVIDER = 0xFFDADDE1;
    static final int LIKE_RED = 0xFFED4956;
    static final int GREEN_ONLINE = 0xFF31A24C;

    static Context ctx;
    static Activity hostActivity;
    static List<Post> posts;
    static User currentUser;
    static android.graphics.Typeface materialFont;

    // Material icon codepoints
    static final String MI_SEARCH = "\uE8B6";
    static final String MI_FAVORITE = "\uE87D";
    static final String MI_CHAT = "\uE0B7";
    static final String MI_ARROW_BACK = "\uE5C4";
    static final String MI_SHARE = "\uE80D";
    static final String MI_THUMB_UP = "\uE8DC";
    static final String MI_SEND = "\uE163";
    static final String MI_HOME = "\uE88A";
    static final String MI_GROUP = "\uE7EF";
    static final String MI_TV = "\uE333";
    static final String MI_STORE = "\uE8D1";
    static final String MI_MENU = "\uE5D2";
    static final String MI_MORE = "\uE5D4";
    static final String MI_PHOTO = "\uE410";
    static final String MI_ADD = "\uE145";
    static final String MI_PERSON = "\uE7FD";

    static TextView materialIcon(String codepoint, int sizeSp, int color) {
        TextView tv = new TextView(ctx);
        tv.setText(codepoint);
        tv.setTextSize(sizeSp);
        tv.setTextColor(color);
        if (materialFont != null) tv.setTypeface(materialFont);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}

        try {
            materialFont = android.graphics.Typeface.createFromAsset(context.getAssets(), "material_icons.ttf");
        } catch (Exception e) {
            try {
                java.io.File f = new java.io.File(context.getCacheDir(), "material_icons.ttf");
                if (f.exists()) materialFont = android.graphics.Typeface.createFromFile(f);
            } catch (Exception e2) {}
        }
        currentUser = new User("You", "\uD83D\uDE0A", 0xFF1877F2);
        initPosts();
    }

    static void initPosts() {
        posts = new ArrayList<>();

        posts.add(new Post(
            new User("Sarah Chen", "\uD83D\uDC69", 0xFFE91E63),
            "Just finished hiking Mount Rainier! The view from the summit was absolutely breathtaking. Nothing beats the feeling of standing above the clouds. \uD83C\uDFD4\uFE0F\u2601\uFE0F",
            "\uD83C\uDF04", // sunrise image placeholder
            "2h", 234, 45, true));

        posts.add(new Post(
            new User("Alex Rivera", "\uD83D\uDC68\u200D\uD83D\uDCBB", 0xFF2196F3),
            "Excited to announce I've joined Google as a Senior Engineer! \uD83C\uDF89 After 6 months of interviews, it finally happened. Thanks to everyone who supported me on this journey!",
            null,
            "4h", 1892, 312, false));

        posts.add(new Post(
            new User("Food Explorer", "\uD83C\uDF73", 0xFFFF9800),
            "Made homemade ramen from scratch today \uD83C\uDF5C\n\nTonkotsu broth (12 hours simmering)\nHandmade noodles\nChashu pork\nSoft-boiled egg\nGreen onions & nori\n\nRecipe in comments!",
            "\uD83C\uDF5C",
            "5h", 567, 89, false));

        posts.add(new Post(
            new User("Dev Community", "\uD83D\uDCBB", 0xFF9C27B0),
            "Poll: What's your favorite programming language in 2026?\n\n\uD83D\uDD35 Rust (42%)\n\uD83D\uDFE2 Python (28%)\n\uD83D\uDFE1 TypeScript (18%)\n\uD83D\uDD34 Go (12%)\n\n4,521 votes so far",
            null,
            "6h", 3241, 891, true));

        posts.add(new Post(
            new User("Travel Diaries", "\u2708\uFE0F", 0xFF00BCD4),
            "Tokyo at night is something else entirely \uD83C\uDF03\n\nShibuya crossing, Akihabara neon lights, and the best street food you'll ever taste. Japan, you have my heart.",
            "\uD83C\uDF03",
            "8h", 1245, 156, false));

        posts.add(new Post(
            new User("Emma Watson", "\uD83D\uDC69\u200D\uD83C\uDFA4", 0xFFFF5722),
            "Reading is the gateway to empathy. Currently on my 47th book this year: 'The Ministry of the Future' by Kim Stanley Robinson. Highly recommend! \uD83D\uDCDA",
            null,
            "12h", 45200, 2100, false));

        posts.add(new Post(
            new User("Cute Pets Daily", "\uD83D\uDC36", 0xFF8BC34A),
            "This golden retriever learned to open the fridge and now he brings everyone snacks \uD83D\uDE02\uD83D\uDE02\uD83D\uDE02\n\nGood boy level: 1000",
            "\uD83D\uDC36",
            "1d", 23400, 4500, true));
    }

    // ═══ Navigation ═══
    static void show(final View view) {
        if (hostActivity == null) return;
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (view.getParent() != null)
                    ((ViewGroup) view.getParent()).removeView(view);
                hostActivity.setContentView(view);
            }
        });
    }

    // ═══ Helpers ═══
    static int dp(int d) { return (int)(d * ctx.getResources().getDisplayMetrics().density); }

    static GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    static GradientDrawable circle(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
        return d;
    }

    static GradientDrawable gradient(int startColor, int endColor) {
        GradientDrawable d = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{startColor, endColor});
        return d;
    }

    static TextView label(String text, int sizeSp, int color) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(sizeSp);
        tv.setTextColor(color);
        return tv;
    }

    static TextView boldLabel(String text, int sizeSp, int color) {
        TextView tv = label(text, sizeSp, color);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    static String formatCount(int n) {
        if (n >= 1000000) return String.format("%.1fM", n / 1000000.0);
        if (n >= 1000) return String.format("%.1fK", n / 1000.0);
        return "" + n;
    }

    // ═══════════════════════════════════════════
    //  MAIN FEED
    // ═══════════════════════════════════════════
    public static void showFeed() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Top bar
        LinearLayout topBar = new LinearLayout(ctx);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(WHITE);
        topBar.setPadding(dp(16), dp(10), dp(16), dp(10));
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setElevation(dp(2));

        TextView logo = boldLabel("socialfeed", 22, PRIMARY);
        logo.setTypeface(Typeface.create("serif", Typeface.BOLD_ITALIC));
        topBar.addView(logo, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Icons
        TextView searchIcon = materialIcon(MI_SEARCH, 22, DARK);
        searchIcon.setPadding(dp(12), 0, dp(12), 0);
        topBar.addView(searchIcon);
        TextView heartIcon = materialIcon(MI_FAVORITE, 22, LIKE_RED);
        heartIcon.setPadding(dp(12), 0, dp(12), 0);
        topBar.addView(heartIcon);
        TextView msgIcon = materialIcon(MI_CHAT, 22, DARK);
        msgIcon.setPadding(dp(12), 0, 0, 0);
        topBar.addView(msgIcon);

        // Back to menu
        Button backBtn = new Button(ctx);
        backBtn.setText("\u2190");
        backBtn.setTextSize(18);
        backBtn.setTextColor(DARK);
        backBtn.setBackgroundColor(Color.TRANSPARENT);
        backBtn.setPadding(0, 0, dp(8), 0);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try { com.example.apklauncher.ApkLauncher.showHome(); }
                catch (Exception e) { com.example.mockdonalds.MockApp.showMenu(); }
            }
        });
        topBar.addView(backBtn, 0);

        root.addView(topBar);

        // Stories bar
        root.addView(buildStoriesBar());

        // Divider
        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        root.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));

        // Create post bar
        root.addView(buildCreatePostBar());

        // Feed list
        ListView feedList = new ListView(ctx);
        feedList.setDivider(null);
        feedList.setDividerHeight(dp(8));
        feedList.setBackgroundColor(BG);
        feedList.setPadding(0, dp(4), 0, dp(4));
        feedList.setAdapter(new BaseAdapter() {
            public int getCount() { return posts.size(); }
            public Object getItem(int p) { return posts.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                return buildPostCard(posts.get(pos), pos);
            }
        });
        root.addView(feedList, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        // Bottom nav
        root.addView(buildBottomNav());

        show(root);
    }

    // ═══ Stories Bar ═══
    static View buildStoriesBar() {
        ScrollView scroll = new ScrollView(ctx) {
            // Horizontal scroll: we'll use a horizontal LinearLayout
        };
        // Since ScrollView is vertical, use a wrapper
        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setBackgroundColor(WHITE);
        wrapper.setPadding(dp(8), dp(12), dp(8), dp(12));

        // Use HorizontalScrollView equivalent — just a horizontal LinearLayout
        LinearLayout storiesRow = new LinearLayout(ctx);
        storiesRow.setOrientation(LinearLayout.HORIZONTAL);
        storiesRow.setPadding(dp(4), 0, dp(4), 0);

        // Your story
        storiesRow.addView(storyBubble("Your Story", "\u2795", 0xFFE0E0E0, false));

        // Other stories
        String[][] stories = {
            {"Sarah", "\uD83D\uDC69", "#E91E63"},
            {"Alex", "\uD83D\uDC68\u200D\uD83D\uDCBB", "#2196F3"},
            {"Emma", "\uD83D\uDC69\u200D\uD83C\uDFA4", "#FF5722"},
            {"Food", "\uD83C\uDF73", "#FF9800"},
            {"Travel", "\u2708\uFE0F", "#00BCD4"},
            {"Pets", "\uD83D\uDC36", "#8BC34A"},
        };
        for (String[] s : stories) {
            storiesRow.addView(storyBubble(s[0], s[1],
                (int)Long.parseLong(s[2].substring(1), 16) | 0xFF000000, true));
        }

        wrapper.addView(storiesRow);
        return wrapper;
    }

    static View storyBubble(String name, String emoji, int ringColor, boolean hasStory) {
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setPadding(dp(8), 0, dp(8), 0);

        // Avatar with ring
        LinearLayout ring = new LinearLayout(ctx);
        ring.setGravity(Gravity.CENTER);
        GradientDrawable ringBg;
        if (hasStory) {
            ringBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0xFFE91E63, 0xFFFF9800, 0xFFFFEB3B});
        } else {
            ringBg = circle(0xFFE0E0E0);
        }
        ringBg.setShape(GradientDrawable.OVAL);
        ring.setBackground(ringBg);

        LinearLayout avatarInner = new LinearLayout(ctx);
        avatarInner.setGravity(Gravity.CENTER);
        avatarInner.setBackground(circle(WHITE));
        LinearLayout.LayoutParams innerLp = new LinearLayout.LayoutParams(dp(56), dp(56));
        avatarInner.setLayoutParams(innerLp);

        LinearLayout avatarCircle = new LinearLayout(ctx);
        avatarCircle.setGravity(Gravity.CENTER);
        avatarCircle.setBackground(circle(ringColor));
        avatarCircle.addView(label(emoji, 22, WHITE));
        avatarCircle.setLayoutParams(new LinearLayout.LayoutParams(dp(50), dp(50)));
        avatarInner.addView(avatarCircle);

        ring.addView(avatarInner);
        ring.setLayoutParams(new LinearLayout.LayoutParams(dp(62), dp(62)));
        col.addView(ring);

        // Name
        TextView nameTv = label(name, 11, SECONDARY);
        nameTv.setGravity(Gravity.CENTER);
        nameTv.setPadding(0, dp(4), 0, 0);
        col.addView(nameTv);

        return col;
    }

    // ═══ Create Post Bar ═══
    static View buildCreatePostBar() {
        LinearLayout bar = new LinearLayout(ctx);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(WHITE);
        bar.setPadding(dp(12), dp(10), dp(12), dp(10));
        bar.setGravity(Gravity.CENTER_VERTICAL);

        // Avatar
        LinearLayout avatar = new LinearLayout(ctx);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(circle(PRIMARY));
        avatar.addView(label("\uD83D\uDE0A", 18, WHITE));
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));
        bar.addView(avatar);

        // Input hint
        TextView hint = label("What's on your mind?", 14, 0xFFBDBDBD);
        hint.setPadding(dp(12), dp(8), dp(12), dp(8));
        hint.setBackground(roundRect(BG, 20));
        bar.addView(hint, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Photo icon
        TextView photoIcon = materialIcon(MI_PHOTO, 22, GREEN_ONLINE);
        photoIcon.setPadding(dp(12), 0, 0, 0);
        bar.addView(photoIcon);

        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp.bottomMargin = dp(8);
        bar.setLayoutParams(blp);

        return bar;
    }

    // ═══ Post Card ═══
    static View buildPostCard(final Post post, final int index) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(WHITE);
        card.setElevation(dp(1));

        // Header: avatar + name + time
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(dp(14), dp(12), dp(14), dp(8));
        header.setGravity(Gravity.CENTER_VERTICAL);

        // Avatar
        LinearLayout avatar = new LinearLayout(ctx);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(circle(post.author.avatarColor));
        avatar.addView(label(post.author.emoji, 18, WHITE));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(42), dp(42));
        alp.rightMargin = dp(10);
        avatar.setLayoutParams(alp);
        header.addView(avatar);

        // Name + time column
        LinearLayout nameCol = new LinearLayout(ctx);
        nameCol.setOrientation(LinearLayout.VERTICAL);
        nameCol.addView(boldLabel(post.author.name, 14, DARK));

        LinearLayout timeRow = new LinearLayout(ctx);
        timeRow.setOrientation(LinearLayout.HORIZONTAL);
        timeRow.setGravity(Gravity.CENTER_VERTICAL);
        timeRow.addView(label(post.time + " \u00B7 ", 12, SECONDARY));
        timeRow.addView(label("\uD83C\uDF0D", 11, SECONDARY)); // public globe
        nameCol.addView(timeRow);

        header.addView(nameCol, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // More button
        TextView moreBtn = materialIcon(MI_MORE, 22, SECONDARY);
        header.addView(moreBtn);

        card.addView(header);

        // Post text
        TextView textView = label(post.text, 14, DARK);
        textView.setPadding(dp(14), 0, dp(14), dp(10));
        textView.setLineSpacing(dp(3), 1);
        card.addView(textView);

        // Image placeholder (if post has image)
        if (post.imageEmoji != null) {
            LinearLayout imgContainer = new LinearLayout(ctx);
            imgContainer.setGravity(Gravity.CENTER);
            imgContainer.setMinimumHeight(dp(220));

            // Gradient background as image placeholder
            int[] gradColors = getGradientForEmoji(post.imageEmoji);
            GradientDrawable imgBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, gradColors);
            imgContainer.setBackground(imgBg);

            // Large emoji as image
            TextView imgEmoji = new TextView(ctx);
            imgEmoji.setText(post.imageEmoji);
            imgEmoji.setTextSize(72);
            imgEmoji.setGravity(Gravity.CENTER);
            imgContainer.addView(imgEmoji);

            card.addView(imgContainer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(220)));
        }

        // Reactions summary
        LinearLayout reactSummary = new LinearLayout(ctx);
        reactSummary.setOrientation(LinearLayout.HORIZONTAL);
        reactSummary.setPadding(dp(14), dp(8), dp(14), dp(8));
        reactSummary.setGravity(Gravity.CENTER_VERTICAL);

        // Reaction emojis
        TextView reactions = label("\uD83D\uDC4D\u2764\uFE0F\uD83D\uDE02 ", 12, SECONDARY);
        reactSummary.addView(reactions);
        reactSummary.addView(label(formatCount(post.likes), 13, SECONDARY),
            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        reactSummary.addView(label(formatCount(post.comments) + " comments", 13, SECONDARY));
        card.addView(reactSummary);

        // Divider
        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1);
        dlp.setMargins(dp(14), 0, dp(14), 0);
        div.setLayoutParams(dlp);
        card.addView(div);

        // Action buttons: Like, Comment, Share
        LinearLayout actions = new LinearLayout(ctx);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setPadding(dp(4), dp(2), dp(4), dp(2));

        final boolean[] liked = {post.liked};

        final TextView likeBtn = actionBtn(
            liked[0] ? "\uD83D\uDC4D Like" : "\uD83D\uDC4D Like",
            liked[0] ? PRIMARY : SECONDARY);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                liked[0] = !liked[0];
                post.liked = liked[0];
                post.likes += liked[0] ? 1 : -1;
                likeBtn.setTextColor(liked[0] ? PRIMARY : SECONDARY);
                likeBtn.setText(liked[0] ? "\uD83D\uDC4D Liked" : "\uD83D\uDC4D Like");
            }
        });
        actions.addView(likeBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView commentBtn = actionBtn("\uD83D\uDCAC Comment", SECONDARY);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showComments(post); }
        });
        actions.addView(commentBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView shareBtn = actionBtn("\u21A9 Share", SECONDARY);
        actions.addView(shareBtn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        card.addView(actions);

        return card;
    }

    static int[] getGradientForEmoji(String emoji) {
        if ("\uD83C\uDF04".equals(emoji)) return new int[]{0xFFFF6B35, 0xFFFF9800, 0xFFFFEB3B};
        if ("\uD83C\uDF5C".equals(emoji)) return new int[]{0xFF8D6E63, 0xFFBCAAA4, 0xFFD7CCC8};
        if ("\uD83C\uDF03".equals(emoji)) return new int[]{0xFF1A237E, 0xFF283593, 0xFF3F51B5};
        if ("\uD83D\uDC36".equals(emoji)) return new int[]{0xFF8BC34A, 0xFFC8E6C9, 0xFFE8F5E9};
        return new int[]{0xFF90CAF9, 0xFFBBDEFB, 0xFFE3F2FD};
    }

    static TextView actionBtn(String text, int color) {
        TextView btn = new TextView(ctx);
        btn.setText(text);
        btn.setTextSize(13);
        btn.setTextColor(color);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(0, dp(10), 0, dp(10));
        return btn;
    }

    // ═══ Comments Screen ═══
    static void showComments(final Post post) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(WHITE);

        // Header
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(WHITE);
        header.setPadding(dp(12), dp(12), dp(16), dp(12));
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setElevation(dp(2));

        Button back = new Button(ctx);
        back.setText(MI_ARROW_BACK);
        if (materialFont != null) back.setTypeface(materialFont);
        back.setTextSize(20);
        back.setTextColor(DARK);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showFeed(); }
        });
        header.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        header.addView(boldLabel("Comments", 18, DARK));
        root.addView(header);

        // Original post (compact)
        LinearLayout postPreview = new LinearLayout(ctx);
        postPreview.setOrientation(LinearLayout.HORIZONTAL);
        postPreview.setPadding(dp(14), dp(10), dp(14), dp(10));
        postPreview.setBackgroundColor(0xFFF5F5F5);

        LinearLayout av = new LinearLayout(ctx);
        av.setGravity(Gravity.CENTER);
        av.setBackground(circle(post.author.avatarColor));
        av.addView(label(post.author.emoji, 14, WHITE));
        av.setLayoutParams(new LinearLayout.LayoutParams(dp(32), dp(32)));
        postPreview.addView(av);

        String preview = post.text.length() > 80 ? post.text.substring(0, 80) + "..." : post.text;
        TextView previewTv = label(post.author.name + ": " + preview, 13, SECONDARY);
        previewTv.setPadding(dp(10), 0, 0, 0);
        postPreview.addView(previewTv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        root.addView(postPreview);

        View div = new View(ctx);
        div.setBackgroundColor(DIVIDER);
        root.addView(div, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));

        // Comments
        ScrollView scroll = new ScrollView(ctx);
        LinearLayout commentsList = new LinearLayout(ctx);
        commentsList.setOrientation(LinearLayout.VERTICAL);
        commentsList.setPadding(dp(14), dp(8), dp(14), dp(8));

        String[][] comments = {
            {"Mike Johnson", "\uD83D\uDC68", "#4CAF50", "This is amazing! \uD83D\uDE0D", "1h", "12"},
            {"Lisa Park", "\uD83D\uDC69", "#9C27B0", "Wow, congrats!! So happy for you \uD83C\uDF89", "45m", "8"},
            {"David Kim", "\uD83D\uDC68\u200D\uD83D\uDD2C", "#FF5722", "Can you share more details? I'd love to know the process", "30m", "3"},
            {"Anna Smith", "\uD83D\uDC69\u200D\uD83C\uDFA8", "#E91E63", "Incredible work! You deserve this \u2764\uFE0F", "15m", "5"},
            {"Tom Wilson", "\uD83E\uDDD4", "#795548", "Following for updates!", "5m", "1"},
        };

        for (String[] c : comments) {
            commentsList.addView(buildComment(c[0], c[1],
                (int)Long.parseLong(c[2].substring(1), 16) | 0xFF000000,
                c[3], c[4], Integer.parseInt(c[5])));
        }

        scroll.addView(commentsList);
        root.addView(scroll, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        // Comment input bar
        LinearLayout inputBar = new LinearLayout(ctx);
        inputBar.setOrientation(LinearLayout.HORIZONTAL);
        inputBar.setPadding(dp(12), dp(8), dp(12), dp(8));
        inputBar.setBackgroundColor(WHITE);
        inputBar.setElevation(dp(4));
        inputBar.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout myAvatar = new LinearLayout(ctx);
        myAvatar.setGravity(Gravity.CENTER);
        myAvatar.setBackground(circle(PRIMARY));
        myAvatar.addView(label("\uD83D\uDE0A", 14, WHITE));
        myAvatar.setLayoutParams(new LinearLayout.LayoutParams(dp(32), dp(32)));
        inputBar.addView(myAvatar);

        TextView inputHint = label("Write a comment...", 14, 0xFFBDBDBD);
        inputHint.setPadding(dp(12), dp(8), dp(12), dp(8));
        inputHint.setBackground(roundRect(BG, 20));
        inputBar.addView(inputHint, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView sendBtn = materialIcon(MI_SEND, 22, PRIMARY);
        sendBtn.setPadding(dp(12), 0, 0, 0);
        inputBar.addView(sendBtn);

        root.addView(inputBar);

        show(root);
    }

    static View buildComment(String name, String emoji, int color, String text, String time, int likes) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(8), 0, dp(8));

        // Avatar
        LinearLayout avatar = new LinearLayout(ctx);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(circle(color));
        avatar.addView(label(emoji, 14, WHITE));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(dp(34), dp(34));
        alp.rightMargin = dp(10);
        avatar.setLayoutParams(alp);
        row.addView(avatar);

        // Comment bubble
        LinearLayout bubble = new LinearLayout(ctx);
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setPadding(dp(12), dp(8), dp(12), dp(8));
        bubble.setBackground(roundRect(BG, 16));

        bubble.addView(boldLabel(name, 13, DARK));
        bubble.addView(label(text, 14, DARK));

        // Time + Like
        LinearLayout meta = new LinearLayout(ctx);
        meta.setOrientation(LinearLayout.HORIZONTAL);
        meta.setPadding(0, dp(4), 0, 0);
        meta.addView(label(time, 11, SECONDARY));
        TextView likeText = boldLabel("  Like", 11, SECONDARY);
        meta.addView(likeText);
        if (likes > 0) {
            meta.addView(label("  \uD83D\uDC4D " + likes, 11, SECONDARY));
        }
        bubble.addView(meta);

        row.addView(bubble, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        return row;
    }

    // ═══ Bottom Nav ═══
    static View buildBottomNav() {
        LinearLayout nav = new LinearLayout(ctx);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(WHITE);
        nav.setElevation(dp(8));
        nav.setPadding(0, dp(6), 0, dp(6));

        String[][] tabs = {
            {MI_HOME, "Home"},
            {MI_GROUP, "Friends"},
            {MI_TV, "Watch"},
            {MI_STORE, "Market"},
            {MI_MENU, "Menu"}
        };

        for (int i = 0; i < tabs.length; i++) {
            LinearLayout tab = new LinearLayout(ctx);
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);

            TextView icon = materialIcon(tabs[i][0], 24, i == 0 ? PRIMARY : SECONDARY);
            tab.addView(icon);

            if (i == 0) {
                // Active indicator
                View indicator = new View(ctx);
                indicator.setBackgroundColor(PRIMARY);
                indicator.setLayoutParams(new LinearLayout.LayoutParams(dp(24), dp(2)));
                tab.addView(indicator);
            }

            tab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            nav.addView(tab);
        }

        return nav;
    }
}
