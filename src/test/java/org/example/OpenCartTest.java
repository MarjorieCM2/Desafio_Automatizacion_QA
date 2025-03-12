package org.example;
import com.github.javafaker.Faker;
import org.openqa.selenium.support.ui.Select;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

public class OpenCartTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Properties properties;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--disable-popup-blocking", "--disable-notifications", "--disable-infobars");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        properties = new Properties();
        try (FileInputStream file = new FileInputStream("src/test/resources/credentials.properties")) {
            properties.load(file);
        } catch (IOException e) {
            System.out.println("ERROR: No se pudo cargar el archivo credentials.properties.");
            System.exit(1);
        }
    }

    private void takeScreenshot(String testName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File destFile = new File("screenshots/" + testName + "_" + timestamp + ".png");
            destFile.getParentFile().mkdirs();
            org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clickElement(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void fillInput(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
    }

    private void addProductToCart(String productName) {
        fillInput(By.name("search"), productName);
        clickElement(By.xpath("//button[contains(@class, 'btn-default')]"));
        clickElement(By.linkText(productName));
        clickElement(By.id("button-cart"));
        takeScreenshot("Add_" + productName.replace(" ", "_"));
    }

    @Test(priority = 1)
    public void addToCartTest() {
        driver.get("http://opencart.abstracta.us/index.php?route=common/home");
        addProductToCart("iPod Classic");
        addProductToCart("iMac");
    }

    @Test(priority = 2)
    public void openViewCart() {
        WebElement cartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("cart-total")));

        // Manejo de StaleElementReferenceException en el botón del carrito
        for (int i = 0; i < 3; i++) {
            try {
                cartButton.click();
                break;
            } catch (StaleElementReferenceException e) {
                System.out.println("El botón del carrito se actualizó, volviendo a buscarlo...");
                cartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("cart-total")));
            }
        }

        clickElement(By.xpath("//a[contains(@href, 'route=checkout/cart')]"));
        wait.until(ExpectedConditions.urlContains("route=checkout/cart"));
        takeScreenshot("Cart_Page_Loaded");

        // Esperar que la tabla del carrito esté presente antes de continuar
        WebElement cartTable = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table[contains(@class, 'table')]")));

        for (String product : new String[]{"iMac", "iPod Classic"}) {
            boolean productFound = false;

            // Intentar encontrar el producto con reintento en caso de StaleElementReferenceException
            for (int i = 0; i < 3; i++) {
                try {
                    WebElement productElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//table[contains(@class, 'table')]//td[@class='text-left']/a[contains(text(), '" + product + "')]")));

                    Assert.assertTrue(productElement.isDisplayed(), "El producto '" + product + "' no está en el carrito.");
                    productFound = true;
                    break;
                } catch (StaleElementReferenceException e) {
                    System.out.println("El producto '" + product + "' se actualizó, volviendo a buscarlo...");
                }
            }

            if (!productFound) {
                System.out.println("Error: No se encontró el producto '" + product + "' en el carrito.");
                System.out.println("Capturando HTML actual de la tabla del carrito...");
                System.out.println(cartTable.getAttribute("outerHTML"));
                takeScreenshot("Error_Cart_" + product.replace(" ", "_"));
                Assert.fail("El producto '" + product + "' no está en el carrito.");
            }
        }

        takeScreenshot("Cart_Validation");
    }



    @Test(priority = 3)
    public void loginAndRegister() {
        String email = properties.getProperty("email");
        String password = properties.getProperty("password");

        driver.get("https://opencart.abstracta.us/index.php?route=account/login");
        fillInput(By.id("input-email"), email);
        fillInput(By.id("input-password"), password);
        clickElement(By.cssSelector("input[type='submit']"));
        takeScreenshot("Login_Submitted");

        // Faker para generar correos dinámicos
        Faker faker = new Faker();
        String dynamicEmail = faker.internet().emailAddress();


        try {
            clickElement(By.linkText("Register"));
            fillInput(By.id("input-firstname"), "Juan");
            fillInput(By.id("input-lastname"), "Sepulveda");
            fillInput(By.id("input-email"), dynamicEmail);
            fillInput(By.id("input-telephone"), "234451234");
            fillInput(By.id("input-password"), "12345678");
            fillInput(By.id("input-confirm"), "12345678");
            clickElement(By.name("agree"));
            clickElement(By.cssSelector("input[type='submit'][value='Continue']"));
        } catch (Exception e) {
            takeScreenshot("Error_General");
            throw e;
        }

        clickElement(By.xpath("//a[contains(@href, 'route=checkout/checkout')]"));
    }




    @Test(priority = 4)
    public void Checkout() {
        fillInput(By.id("input-payment-firstname"), "Juan");
        fillInput(By.id("input-payment-lastname"), "Sepulveda");
        fillInput(By.id("input-payment-address-1"), "Calle falsa 123");
        fillInput(By.id("input-payment-city"), "Santiago");
        fillInput(By.id("input-payment-postcode"), "8320000");

        WebElement countryDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("input-payment-country")));
        Select countrySelect = new Select(countryDropdown);
        countrySelect.selectByValue("43"); // Chile

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#input-payment-zone option[value='671']")));

        WebElement stateDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("input-payment-zone")));
        Select stateSelect = new Select(stateDropdown);
        stateSelect.selectByValue("671");

        try {
            WebElement continueButtonPayment = wait.until(ExpectedConditions.elementToBeClickable(By.id("button-payment-address")));
            continueButtonPayment.click();
            wait.until(ExpectedConditions.stalenessOf(continueButtonPayment));
        } catch (StaleElementReferenceException e) {
            System.out.println("Botón 'Continue' desapareció, volviendo a buscarlo...");
            WebElement continueButtonPayment = wait.until(ExpectedConditions.elementToBeClickable(By.id("button-payment-address")));
            continueButtonPayment.click();
        }

        clickElement(By.id("button-shipping-address"));

        WebElement shippingRadioButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='shipping_method' and @value='flat.flat']")));
        Assert.assertTrue(shippingRadioButton.isSelected(), "El método de envío no está seleccionado automáticamente.");

        WebElement shippingLabel = shippingRadioButton.findElement(By.xpath("./parent::label"));
        String shippingText = shippingLabel.getText().trim();
        Assert.assertTrue(shippingText.contains("Flat Shipping Rate - $5.00"), "El método de envío no es el esperado.");

        System.out.println("Validación de método de envío completada: " + shippingText);
        takeScreenshot("Shipping_Method");

        clickElement(By.id("button-shipping-method"));

        WebElement agreeCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.name("agree")));
        if (!agreeCheckbox.isSelected()) {
            agreeCheckbox.click();
        }

        clickElement(By.id("button-payment-method"));

        try {
            WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tfoot//td[contains(@class, 'text-right')]")
            ));
            String orderTotal = totalElement.getText().trim();
            System.out.println("Total de la orden: " + orderTotal);
            takeScreenshot("Order_Total");
        } catch (TimeoutException e) {
            System.out.println("ERROR: No se encontró el total de la orden.");
            throw e;
        }
    }



    @Test(priority = 5)
    public void validateOrderHistory() {
        try {
            clickElement(By.id("button-confirm"));
        } catch (TimeoutException e) {
            System.out.println("El botón 'Confirm Order' no apareció, es posible que la orden ya esté confirmada.");
        }

        wait.until(ExpectedConditions.urlMatches(".*route=checkout/success.*"));
        clickElement(By.xpath("//a[contains(@href, 'route=account/account')]"));
        clickElement(By.xpath("//a[contains(@href, 'route=account/order')]"));
        wait.until(ExpectedConditions.urlContains("route=account/order"));
        clickElement(By.xpath("//a[contains(@href, 'route=account/order/info')]"));

        WebElement statusElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[@class='text-left']")));

        String orderStatus = statusElement.getText().trim();
        System.out.println("Estado de la orden: " + orderStatus);
        Assert.assertTrue(orderStatus.contains("Pending"), "La orden no está en estado 'Pending");

        takeScreenshot("Order_Status");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
