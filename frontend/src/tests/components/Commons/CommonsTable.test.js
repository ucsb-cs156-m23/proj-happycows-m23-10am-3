import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import CommonsTable from "main/components/Commons/CommonsTable"
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import commonsPlusFixtures from "fixtures/commonsPlusFixtures";
import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/commonsUtils"

// Next line uses technique from https://www.chakshunyu.com/blog/how-to-spy-on-a-named-import-in-jest/
import * as useBackendModule from "main/utils/useBackend";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate
}));

describe("UserTable tests", () => {
  const queryClient = new QueryClient();

  Object.defineProperty(window, "location", {
    value: {
      href: ""
    },
    writable:true
  })

  test("renders without crashing for empty table with user not logged in", () => {
    const currentUser = null;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });
  test("renders without crashing for empty table for ordinary user", () => {
    const currentUser = currentUserFixtures.userOnly;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("renders without crashing for empty table for admin", () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("Has the expected column headers and content for adminUser", () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );

    const expectedHeaders = ["id", "Name", "Cow Price", 'Milk Price', 'Starting Balance', 'Starting Date', 'Degradation Rate', 'Carrying Capacity', 'Cows', 'Show Leaderboard?'];
    const expectedFields = ["id", "name", "cowPrice", "milkPrice", "startingBalance", "startingDate", "degradationRate", "carryingCapacity"];
    const testId = "CommonsTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-commons.${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-commons.id`)).toHaveTextContent("1");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.id`)).toHaveTextContent("2");


    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.name`)).toHaveTextContent("Com2");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.cowPrice`)).toHaveTextContent("1");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.milkPrice`)).toHaveTextContent("2");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.degradationRate`)).toHaveTextContent("0.01");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.carryingCapacity`)).toHaveTextContent("42");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.startingBalance`)).toHaveTextContent("10");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.startingDate`)).toHaveTextContent(/^2022-11-22$/); // regex so that we have an exact match https://stackoverflow.com/a/73298371
    expect(screen.getByTestId(`${testId}-cell-row-1-col-commons.showLeaderboard`)).toHaveTextContent("true");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-totalCows`)).toHaveTextContent("0");

    expect(screen.getByTestId(`${testId}-cell-row-0-col-Edit-button`)).toHaveClass("btn-primary");
    expect(screen.getByTestId(`${testId}-cell-row-0-col-Delete-button`)).toHaveClass("btn-danger");
    expect(screen.getByTestId(`${testId}-cell-row-0-col-Leaderboard-button`)).toHaveClass("btn-secondary");
    expect(screen.getByTestId(`${testId}-cell-row-0-col-Download-button`)).toHaveClass("btn-success");
  });
});

describe("CommonsTable Modal tests", () => {
  const queryClient = new QueryClient();
  const mockMutate = jest.fn();
  const mockUseBackendMutation = {
    mutate: mockMutate,
  };

  beforeEach(() => { jest.spyOn(useBackendModule, "useBackendMutation").mockReturnValue(mockUseBackendMutation) });

  afterEach(() => { jest.clearAllMocks() });
  
  test("Renders without crashing for empty table with user not logged in", () => {
    const currentUser = null;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    expect(mockMutate).not.toHaveBeenCalled();
  });

  test("Clicking the delete button opens the modal for adminUser", async () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(document.body).not.toHaveClass('modal-open');
    });

    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);

    await waitFor(() => {
      expect(document.body).toHaveClass('modal-open');
    });

    expect(mockMutate).not.toHaveBeenCalled();
  });

  test("Clicking yes in the modal popup deletes the commons", async () => {
    const currentUser = currentUserFixtures.adminUser;
    const useBackendMutationSpy = jest.spyOn(useBackendModule, 'useBackendMutation');

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );


    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);

    const yesDeleteButton = await screen.findByTestId("CommonsTable-Modal-YesDelete");
    fireEvent.click(yesDeleteButton);

    await waitFor(() => {
      expect(useBackendMutationSpy).toHaveBeenCalledWith(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/commons/allplus"]
      );
    });

    await waitFor(() => { expect(document.body).not.toHaveClass('modal-open') });
  });

  test("Clicking no in the modal popup deletes the commons", async () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);

    const noDeleteButton = await screen.findByTestId("CommonsTable-Modal-NoDelete");
    fireEvent.click(noDeleteButton);

    await waitFor(() => { expect(document.body).not.toHaveClass('modal-open') });

    expect(mockMutate).not.toHaveBeenCalled();
  });

  test("Pressing the escape key on the modal cancels the deletion", async () => {
    const currentUser = currentUserFixtures.adminUser;
  
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );
  

    const deleteButton = screen.getByTestId("CommonsTable-cell-row-0-col-Delete-button");
    fireEvent.click(deleteButton);
  
    expect(document.body).toHaveClass('modal-open');
  
    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);
  
    await waitFor(() => { expect(document.body).not.toHaveClass('modal-open') });
  
    expect(mockMutate).not.toHaveBeenCalled();
  });
  
  

  test("the download button works as intended", async () => {

    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CommonsTable commons={commonsPlusFixtures.threeCommonsPlus} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId("CommonsTable-cell-row-0-col-Download-button")).toBeInTheDocument();
    });

    const downloadButton = screen.getByTestId("CommonsTable-cell-row-0-col-Download-button");
    fireEvent.click(downloadButton);

    await waitFor(() => {
      expect(window.location.href).toBe("/api/commons/1/download?commonsId=1")
    });

  });


});
