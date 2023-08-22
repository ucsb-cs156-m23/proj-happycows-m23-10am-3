import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import {QueryClient, QueryClientProvider} from "react-query";
import {MemoryRouter} from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import AdminCreateCommonsPage from "main/pages/AdminCreateCommonsPage";
import {apiCurrentUserFixtures} from "fixtures/currentUserFixtures";
import {systemInfoFixtures} from "fixtures/systemInfoFixtures";
import healthUpdateStrategyListFixtures from "../../fixtures/healthUpdateStrategyListFixtures";

const curr = new Date();
const today = curr.toISOString().substring(0, 10);
const onemonthfromtoday = new Date(curr.getFullYear(), curr.getMonth()+1, curr.getDate()).toISOString().substring(0, 10);

const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        Navigate: (x) => { mockedNavigate(x); return null; }
    };
});

const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

describe("AdminCreateCommonsPage tests", () => {
    const axiosMock = new AxiosMockAdapter(axios);
    const queryClient = new QueryClient();

    beforeEach(() => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);

        axiosMock.onGet("/api/commons/all-health-update-strategies")
            .reply(200, healthUpdateStrategyListFixtures.simple);
    });

    test("renders without crashing", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <AdminCreateCommonsPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        expect(await screen.findByText("Create Commons")).toBeInTheDocument();
    });

    test("When you fill in form and click submit, the right things happens", async () => {
        axiosMock.onPost("/api/commons/new").reply(200, {
            "id": 5,
            "name": "My New Commons",
            "cowPrice": 10,
            "milkPrice": 5,
            "startingBalance": 500,
            "startingDate": "2023-08-20T00:00:00.000Z",
            "endingDate": "2023-09-20T00:00:00.000Z",
            "degradationRate": 30.4,
            "carryingCapacity": 25,
            "aboveCapacityHealthUpdateStrategy": "strat2",
            "belowCapacityHealthUpdateStrategy": "strat3",
            "showLeaderboard": false,
        });

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <AdminCreateCommonsPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        expect(await screen.findByText("Create Commons")).toBeInTheDocument();

        const commonsNameField = screen.getByLabelText("Commons Name");
        const startingBalanceField = screen.getByLabelText("Starting Balance");
        const cowPriceField = screen.getByLabelText("Cow Price");
        const milkPriceField = screen.getByLabelText("Milk Price");
        const startDateField = screen.getByLabelText("Starting Date");
        const endDateField = screen.getByLabelText("Ending Date");
        const degradationRateField = screen.getByLabelText("Degradation Rate");
        const carryingCapacityField = screen.getByLabelText("Carrying Capacity");
        const aboveCapacityHealthUpdateStrategyField = screen.getByLabelText("When above capacity");
        const belowCapacityHealthUpdateStrategyField = screen.getByLabelText("When below capacity");
        const showLeaderboardField = screen.getByLabelText("Show Leaderboard?");
        const button = screen.getByTestId("CommonsForm-Submit-Button");

        fireEvent.change(commonsNameField, { target: { value: 'My New Commons' } })
        fireEvent.change(startingBalanceField, { target: { value: '500' } })
        fireEvent.change(cowPriceField, { target: { value: '10' } })
        fireEvent.change(milkPriceField, { target: { value: '5' } })
        fireEvent.change(startDateField, { target: { value: "2023-08-20" } })
        fireEvent.change(endDateField, { target: { value: "2023-09-20" } })
        fireEvent.change(degradationRateField, { target: { value: '30.4' } })
        fireEvent.change(carryingCapacityField, { target: { value: '25' } })
        fireEvent.change(showLeaderboardField, { target: { value: true } })

        fireEvent.change(aboveCapacityHealthUpdateStrategyField, { target: {value: 'strat2' } })
        fireEvent.change(belowCapacityHealthUpdateStrategyField, { target: {value: 'strat3' } })
        fireEvent.click(button);

        await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

        // The Date object is initialized from the form without time information. I believe React
        // Query calls toISOString() before stuffing it into the body of the POST request, so the
        // POST contains the suffix .000Z, which Java's LocalDateTime.parse ignores. [1]

        const expectedCommons = {
            name: "My New Commons",
            startingBalance: 500,
            cowPrice: 10,
            milkPrice: 5,
            startingDate: "2023-08-20T00:00:00.000Z", // [1]
            endingDate: "2023-09-20T00:00:00.000Z",
            degradationRate: 30.4,
            carryingCapacity: 25,
            showLeaderboard: false,
            aboveCapacityHealthUpdateStrategy: "strat2",
            belowCapacityHealthUpdateStrategy: "strat3",
        };

        expect(axiosMock.history.post[0].data).toEqual( JSON.stringify(expectedCommons) );

        expect(mockToast).toBeCalledWith(<div>Commons successfully created!
            <br />id: 5
            <br />name: My New Commons
            <br />startDate: 2023-08-20T00:00:00.000Z
            <br />endDate: 2023-09-20T00:00:00.000Z
            <br />cowPrice: 10
            <br />carryingCapacity: 25
        </div>);

        expect(mockedNavigate).toBeCalledWith({"to": "/"});
    });
});
