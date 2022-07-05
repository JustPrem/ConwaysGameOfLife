import java.awt.*;
import java.awt.image.BufferStrategy;
import java.time.*;
import java.util.Random;

public class WindowCanvas extends Canvas implements Runnable
{
    // Run Variables.
    private Thread thread;
    private Instant lastTick;
    private Instant lastRender;

    // Screen Size.
    private Dimension screenSize = new Dimension(960, 640);

    // Stat Variables.
    private int runCounter = 0;

    // Step Variables.
    private int speedPerSecond;

    // World Variables.
    private Dimension gridSize;
    private Dimension unitSize;

    private boolean[][] cells;
    private boolean[][] newCells;

    //region Constructor.
    public WindowCanvas(int StepPerSecond, int GridSizeX, int GridSizeY)
    {
        this.setBackground(Color.LIGHT_GRAY);
        this.setPreferredSize(screenSize);

        this.unitSize = new Dimension(screenSize.width / GridSizeX, screenSize.height / GridSizeY);
        this.speedPerSecond = StepPerSecond;
        this.gridSize = new Dimension(GridSizeX, GridSizeY);

        this.setVisible(true);

        Start();
    }
    //endregion
    //region Run Start and Stop Methods.
    public void Start()
    {
        thread = new Thread(this, "Program");
        thread.start();
    }
    public void Stop() throws InterruptedException
    {
        thread.join();
    }
    //endregion
    //region Run, Update and Render Methods.
    @Override
    public void run()
    {
        // Wait quarter of a second to initialise the Graphics system.
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Begin();
        // Assign the most recent ticks as the current time.
        Update();
        lastTick = Instant.now();
        Render();
        lastRender = Instant.now();

        while(true)
        {
            if (Duration.between(lastTick, Instant.now()).toMillis() > (1000 / speedPerSecond))
            {
                lastTick = Instant.now();
                Update();
                runCounter += 1;
            }

            if (Duration.between(lastRender, Instant.now()).toMillis() > (1000 / 30))
            {
                lastRender = Instant.now();
                Render();
            }
        }
    }
    /** Begin is ran at the start of the program. */
    private void Begin()
    {
        cells = new boolean[gridSize.width][gridSize.height];

        GenerateRandomCells();
    }
    /** Update is ran every "speedPerSecond" per second (Default is 1 per second) */
    private void Update()
    {
        newCells = new boolean[gridSize.width][gridSize.height];

        // Check All Cells.
        for (int x = 0; x < gridSize.width; x++)
        {
            for (int y = 0; y < gridSize.height; y++)
            {
                CheckCellNeighbours(x, y);
            }
        }

        cells = newCells;
    }
    /** Render is ran around 30 times a second, this simulates a 30fps display */
    private void Render() {
        // Attempt to obtain a BufferStrategy.
        BufferStrategy bufferStrategy = this.getBufferStrategy();

        // Check if a BufferStrategy exists.
        if (bufferStrategy == null) {
            // Create a Buffer Strategy, with 1 frame lookahead.
            this.createBufferStrategy(2);
            return;
        }

        // Check if the BufferStrategy exists.
        if (bufferStrategy != null) {
            // Get Graphics from BufferStrategy.
            Graphics g = bufferStrategy.getDrawGraphics();

            //regio Draw Background.
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, screenSize.width, screenSize.height);

            //region Render Live Cells.
            g.setColor(Color.darkGray);
            for (int x = 0; x < gridSize.width; x++) {
                for (int y = 0; y < gridSize.height; y++) {
                    if (cells[x][y]) {
                        g.fillRect(x * unitSize.width, y * unitSize.height, unitSize.width, unitSize.height);
                    }
                }
            }
            //endregion
            //region Render Grid.
            g.setColor(Color.black);

            for (int x = 1; x < screenSize.width / unitSize.width; x++) {
                for (int y = 1; y < screenSize.height / unitSize.height; y++) {
                    g.drawLine(0, y * unitSize.height, screenSize.width, y * unitSize.height);
                    g.drawLine(x * unitSize.width, 0, x * unitSize.width, screenSize.height);
                }
            }
            //endregion

            g.dispose();
            bufferStrategy.show();
        }
    }

    //endregion

    /** Generates a Random Board of Cells */
    private void GenerateRandomCells()
    {
        Random random = new Random();

        for (int x = 0; x < gridSize.width; x++)
        {
            for (int y = 0; y < gridSize.height; y++)
            {
                cells[x][y] = random.nextBoolean();
            }
        }
    }
    /** Check the neighbours of the cell positioned at X-Y. */
    private void CheckCellNeighbours(int x, int y)
    {
        int aliveNeighbours = 0;

        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                if ((x + i >= 0 && x + i < gridSize.width) && (y + j >= 0 && y + j < gridSize.height))
                {
                    if (cells[x + i][y + j])
                    {
                        aliveNeighbours += 1;
                    }
                }
            }
        }

        if (cells[x][y])
        {
            aliveNeighbours -= 1;
        }

        // Cell is Lonely and Dies.
        if (cells[x][y] && (aliveNeighbours < 2))
        {
            newCells[x][y] = false;
        }
        // Cell dies due to over population.
        else if (cells[x][y] && (aliveNeighbours > 3))
        {
            newCells[x][y] = false;
        }
        // Cell is born.
        else if (!(cells[x][y]) && (aliveNeighbours == 3))
        {
            newCells[x][y] = true;
        }
        else
        {
            newCells[x][y] = cells[x][y];
        }
    }
}