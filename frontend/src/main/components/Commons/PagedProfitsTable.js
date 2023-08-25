
import React from "react";
import OurTable from "main/components/OurTable";
import { Button } from "react-bootstrap";
import { useBackend } from "main/utils/useBackend";
import { timestampToDate } from "main/utils/dateUtils";

export default function PagedProfitsTable({ profits }) {

    const testId = "PagedProfitsTable";
    const refreshProfitsIntervalMilliseconds = 5000;

    const [selectedPage, setSelectedPage] = React.useState(0);

    const pageSize = 5;

    const profitsForTable =
        profits ?
        profits.map(profit => ({
            date: timestampToDate(profit.timestamp),
            ...profit
        })) : 
        // Stryker disable next-line ArrayDeclaration : no need to test what happens if [] is replaced with ["Stryker was here"]
        [];
        profitsForTable.reverse();

    // Stryker disable all
    const {
        data: page
    } = useBackend(
        ["/api/profits/all/commonsid"],
        {
            method: "GET",
            url: "/api/profits/all/commonsid/pageable",
            params: {
                page: selectedPage,
                size: pageSize,
            }
        },
        {content: [], totalPages: 0},
        { refetchInterval: refreshProfitsIntervalMilliseconds }
    );
    // Stryker restore  all

    const testid = "PagedProfitsTable";

    const previousPageCallback = () => {
        return () => {
            setSelectedPage(selectedPage - 1);
        }
    }

    const nextPageCallback = () => {
        return () => {
            setSelectedPage(selectedPage + 1);
        }
    }
    
    const memoizedColumns = React.useMemo(() => 
        [
            {
                Header: "Profit",
                accessor: (row) => `$${row.amount.toFixed(2)}`,
            },
            {
                Header: "Date",
                accessor: "date",
            },
            {
                Header: "Health",
                accessor: (row) => `${row.avgCowHealth + '%'}`
            },
            {
                Header: "Cows",
                accessor: "numCows",
            },
        ], 
    []);

    const memoizedDates = React.useMemo(() => profits, [profits]);
    // Stryker restore ArrayDeclaration

    return (
        <>
            < OurTable
                data={page.content}
                columns={memoizedColumns}
                testid={testid}
                initialState={{ sortBy: memoizedDates }}
            />
            <p></p>
            <p>
            <Button data-testid={`${testId}-previous-button`}onClick={previousPageCallback()} disabled={ selectedPage === 0}>Previous</Button>
            &nbsp; Page: {selectedPage + 1} &nbsp;
            <Button data-testid={`${testId}-next-button`} onClick={nextPageCallback()} disabled={page.totalPages===0 || selectedPage === page.totalPages-1}>Next</Button>
            </p>
            
        </>
    );
}; 
