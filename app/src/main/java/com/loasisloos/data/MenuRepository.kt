package com.loasisloos.data

object MenuRepository {
    
    // --- lists of options ---
    private val sauces = listOf(
        "Blanche", "Algérienne", "Andalouse", "Samouraï", "Hannibal", 
        "Mayonnaise", "Ketchup", "Cocktail", "Barbecue", "Américaine", 
        "Harissa", "Biggy Burger", "Poivre", "Curry", "Moutarde"
    )

    private val meats = listOf(
        "Chicken Tikka", "Kébab", "Chicken Oriental", "Cordon Bleu", 
        "Chicken Boursin", "Tenders", "Mexicanos", "Merguez", 
        "Steak Haché", "Kefta", "Végétarien", "Fricadelle"
    )

    private val supplements = listOf(
        Supplement("sup_cheddar", "Cheddar", 1.0),
        Supplement("sup_vqr", "Vache qui rit", 1.0),
        Supplement("sup_feta", "Fromage Fêta", 1.0),
        Supplement("sup_chevre", "Chèvre", 1.0),
        Supplement("sup_boursin", "Boursin", 1.0),
        Supplement("sup_oeuf", "Oeuf", 1.0),
        Supplement("sup_crudite", "Crudité", 1.0),
        Supplement("sup_sauce_fromagere", "Sauce Fromagère", 1.5),
        Supplement("sup_viande", "Viande Supp.", 2.0)
    )

    // --- Categories ---

    val categories = listOf(
        Category("cat_burger", "Burger", null, listOf(
            Product("b_cheese", "Cheese Burger", null, 5.0, "cat_burger", hasComboOptions = true),
            Product("b_chicken", "Chicken Burger", null, 6.5, "cat_burger", hasComboOptions = true),
            Product("b_fish", "Fish Burger", null, 6.5, "cat_burger", hasComboOptions = true),
            Product("b_double", "Double Cheese", null, 6.5, "cat_burger", hasComboOptions = true),
            Product("b_triple", "Triple Cheese", null, 7.5, "cat_burger", hasComboOptions = true)
        )),
        
        Category("cat_panini", "Panini", null, listOf(
            Product("p_poulet", "Panini Poulet", null, 5.0, "cat_panini", hasComboOptions = true),
            Product("p_steak", "Panini Steak", null, 5.0, "cat_panini", hasComboOptions = true),
            Product("p_3fromages", "Panini 3 Fromages", null, 5.0, "cat_panini", hasComboOptions = true),
            Product("p_kebab", "Panini Kébab", null, 5.0, "cat_panini", hasComboOptions = true),
            Product("p_nutella", "Panini Nutella", null, 4.0, "cat_panini", hasComboOptions = false)
        )),

        Category("cat_tacos", "Tacos", null, listOf(
            Product("t_m", "Tacos M (1 Viande)", null, 7.0, "cat_tacos", 
                hasMeatSelection = true, maxMeats = 1, hasSauceSelection = true, hasSupplements = true, hasGratinéOptions = true),
            Product("t_l", "Tacos L (2 Viandes)", null, 8.0, "cat_tacos", 
                hasMeatSelection = true, maxMeats = 2, hasSauceSelection = true, hasSupplements = true, hasGratinéOptions = true),
            Product("t_xl", "Tacos XL (2 Viandes)", null, 12.0, "cat_tacos", 
                hasMeatSelection = true, maxMeats = 2, hasSauceSelection = true, hasSupplements = true, hasGratinéOptions = true),
            Product("t_xxl", "Tacos XXL (3 Viandes)", null, 15.0, "cat_tacos", 
                hasMeatSelection = true, maxMeats = 3, hasSauceSelection = true, hasSupplements = true, hasGratinéOptions = true)
        )),

        Category("cat_sandwich", "Sandwichs Chauds", null, listOf(
            Product("s_1viande", "Sandwich 1 Viande", null, 6.5, "cat_sandwich", 
                hasBreadOptions = true, hasMeatSelection = true, maxMeats = 1, hasSauceSelection = true),
            Product("s_2viandes", "Sandwich 2 Viandes", null, 8.0, "cat_sandwich", 
                hasBreadOptions = true, hasMeatSelection = true, maxMeats = 2, hasSauceSelection = true),
            Product("s_naan", "Naan Seul", null, 2.5, "cat_sandwich")
        )),
        
        Category("cat_assiette", "Assiette", null, listOf(
             Product("a_1viande", "Assiette 1 Viande", null, 9.5, "cat_assiette", hasMeatSelection = true, maxMeats = 1, hasSauceSelection = true),
             Product("a_3viandes", "Assiette 3 Viandes", null, 13.5, "cat_assiette", hasMeatSelection = true, maxMeats = 3, hasSauceSelection = true)
        )),
        
        Category("cat_boisson", "Boissons", null, listOf(
            Product("d_soda33", "Soda 33cl", null, 1.5, "cat_boisson"),
            Product("d_oasis", "Oasis 2L", null, 4.5, "cat_boisson"),
            Product("d_redbull", "Redbull", null, 2.5, "cat_boisson"),
            Product("d_caprisun", "Capri Sun", null, 1.0, "cat_boisson")
        ))
    )
    
    // Helper to get options
    fun getMeats() = meats
    fun getSauces() = sauces
    fun getSupplements() = supplements
    fun getBreads() = listOf("Galette", "Naan", "Falluche")
    fun getGratinéOptions() = listOf("Mozzarella", "Chèvre", "Raclette", "Chorizo")
}
